package ac.webapp.react

import ac.syntax._
import ac.interactions.{Command, State => SessionState}
import japgolly.scalajs.react._
import monix.execution.Scheduler
import monix.reactive.Observable
import vdom.html_<^._

object Arcomage {
  case class Props (
    scheduler: Scheduler,
    onCommand: Command => Unit,
    states: Observable[SessionState]
  ) {
    def render = Component(this)
  }

  case class State (current: SessionState)

  val Component = ScalaComponent.builder[Props]("Arcomage")
    .initialState(State(SessionState.Initial))
    .renderBackend[Backend]
    .componentWillMount(i => i.backend.connectState(i.props))
    .build

  class Backend($: BackendScope[Props, State]) {
    def connectState(p: Props): Callback = Callback {
      discard {
        println("WillMount")
        p.states.foreach(ss => $.modState(_.copy(current = ss)).runNow())(p.scheduler)
      }
    }

    def render(s: State, p: Props) = {
      <.div(s.toString)
    }
  }
}
