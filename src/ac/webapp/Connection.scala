package ac.webapp

import ac.communication._
import ac.interactions._
import ac.syntax._
import ac.webapp.PeerCommand._
import cats.instances.option._
import cats.syntax.apply._
import monix.cats._
import monix.eval.Task
import monix.reactive.Observable
import monix.reactive.subjects.{PublishSubject, ReplaySubject}


class Connection (protocol: Protocol[Observable, Task, Result], outcome: OutcomeFn[Task]) {
  private[this] val local = PublishSubject[PeerCommand]
  private[this] val cmds = ReplaySubject[Observable[PeerCommand]](local)
  private[this] var sendFunc: Option[Result => Task[Unit]] = None

  private[this] def registerChannel(ch: protocol.Channel) = {
    require(sendFunc.isEmpty)

    cmds.onNext(ch.received.map(Run))
    cmds.onComplete()
    sendFunc = Some(ch.send _)
  }

  def open: Task[(Observable[App.State], PeerCommand => Unit)] = Task.deferAction { implicit sc =>
    val states = cmds
      .merge
      .scan(Task.pure(App.State())) { (stateL, peerCmd) =>
        val nextStateL = peerCmd match {
          case Host =>
            stateL.zip(protocol.makeOffer).map { case (state, (offer, channelL)) =>
              channelL.foreach(registerChannel)
              state.copy(State.HostNameEntry, offer)
            }

          case Join(offer) => stateL.map { state =>
            protocol.connect(offer).foreach(registerChannel)
            state.copy(State.AwaitHostConnection, "")
          }

          case Run(result) =>
            for {
              state             <- stateL
              procResult        <- result.process(outcome).run(state.current)
              (updated, toSend) =  procResult
              _                 <- sendFunc.ap(toSend) getOrElse Task.unit
            } yield state.copy(updated)
        }

        nextStateL.memoize
      }
      .mapTask(identity)
      .share

    Task.pure((states, cmd => discard { local.onNext(cmd) }))
  }
}
