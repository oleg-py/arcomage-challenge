package ac.webapp

import scala.concurrent.duration._

import ac.interactions._
import ac.syntax._
import ac.webapp.react.Arcomage
import japgolly.scalajs.react.Callback
import monix.cats._
import monix.eval.{Task, TaskApp}
import monix.execution.Scheduler
import monix.reactive.Observable
import monix.reactive.subjects.PublishSubject
import org.scalajs.dom.document

object Main extends TaskApp with Algebras {
  override def runc = Task.deferAction { implicit sc =>
    val (states, onCommand) = wire(_ => Task.unit, Observable.never)
    val target = document.getElementById("app-root")

    Arcomage.Props(onCommand, register(states))
      .render
      .renderIntoDOM(target)

    testSequence(
      Command.JoinInitiated,
      Command.EnemyNameSet("Enemy - Host"),
      Command.NameEntered("Player - Guest")
    ).foreach(onCommand)

    Task.unit
  }

  private def register(states: Observable[State])(fn: State => Callback)(implicit sc: Scheduler) = {
    discard { states.foreach(s => fn(s).attemptTry.runNow().get) }
  }

  private def wire(send: Result => Task[Unit], received: Observable[Result])(implicit sc: Scheduler) = {
    val local = PublishSubject[Result]()
    val onCommand = (c: Command) => discard { local.onNext(Result.Cmd(c)) }
    val outcomeFn = ValidTransitions.table[Task]

    /*_*/
    val states = Observable(local).merge
      .scan(Task.pure[State](State.Initial)) { (stateL, result) =>
        stateL.flatMap(result.process(outcomeFn).run).flatMap {
          case (state, maybeResult) =>
            maybeResult.map(send).getOrElse(Task.unit).map(_ => state)
        }
      }
      .mapTask(identity)
      .behavior(State.Initial)
      .refCount // <- this bit is important

    (states, onCommand)
  }

  def testSequence(cmd: Command*) =
    Observable.fromIterable(cmd)
    .mapTask(Task.pure(_).delayExecution(1.second))

}
