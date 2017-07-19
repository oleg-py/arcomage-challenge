package ac

import scala.util.Random

import ac.communication._
import ac.game.Randomizer
import ac.interactions.{Result, State, Transitions}
import ac.syntax._
import ac.webapp.PeerCommand.{Host, Join, Run}
import cats.syntax.apply._
import cats.instances.option._
import monix.cats._
import monix.eval.Task
import monix.reactive.Observable
import monix.reactive.subjects.{PublishSubject, ReplaySubject}
import monix.execution.Scheduler
import monix.execution.atomic.AtomicInt

package object webapp {
  implicit object TaskToObservable extends Collectable[Observable, Task] {
    override def collectFirstM[A, B](o: Observable[A])(f: PartialFunction[A, B]): Task[B] =
      o.collect(f).headL

    override def collectM_[A, B](o: Observable[A])(f: PartialFunction[A, Task[B]]): Task[Unit] =
      o.collect(f).mapTask(identity).lastL.map(discard)
  }

  implicit object ImpureRandomizer extends Randomizer[Task] {
    override def shuffles[A](v: TraversableOnce[A]): Task[Stream[A]] =
      Task.evalOnce { Stream.continually { Random.shuffle(v) }.flatten }
  }

  implicit object TaskUniqueId extends UniqueId[Task] {
    private val counter = AtomicInt(0)
    override def generate: Task[Int] = Task.eval { counter.incrementAndGet() }
  }

  def connect(
    protocol: Protocol[Observable, Task, Result]
  )(implicit
    sc: Scheduler
  ): (Observable[RootState], PeerCommand => Unit) = {

    val local = PublishSubject[PeerCommand]
    val cmds = ReplaySubject[Observable[PeerCommand]](local.share)

    var sendOpt: Option[Result => Task[Unit]] = None
    def registerChannel(chL: Task[protocol.Channel]) = discard {
      chL.foreach { ch =>
        cmds.onNext(ch.received.map(Run).share)
        cmds.onComplete()
        sendOpt = Some(ch.send _)
      }
    }

    val states = cmds.merge
      .scan(Task.pure(RootState())) { (stateL, peerCmd) =>
        peerCmd match {
          case Host =>
            stateL.zip(protocol.makeOffer).map {
              case (state, (offer, channelL)) =>
                registerChannel(channelL)
                state.copy(current = State.HostNameEntry, offer = offer.string)
            }

          case Join(offer) => stateL.map { state =>
            registerChannel(protocol.connect(Offer(offer)))
            state.copy(current = State.AwaitHostConnection, offer = "")
          }

          case Run(result) =>
            for {
              state             <- stateL
              procResult        <- result.process(Transitions[Task]).run(state.current)
              (updated, toSend) = procResult
              _                 <- sendOpt.ap(toSend) getOrElse Task.unit
            } yield state.copy(current = updated)
        }
      }
      .mapTask(identity)
      .behavior(RootState())
      .refCount

    (states, cmd => discard { local.onNext(cmd) })
  }
}
