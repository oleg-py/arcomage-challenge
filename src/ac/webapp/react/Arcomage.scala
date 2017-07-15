package ac.webapp.react

import ac.interactions.{Command, State => SessionState}
import monocle.macros._
import japgolly.scalajs.react._
import vdom.html_<^._

object Arcomage {
  case class Props (
    onCommand: Command => Unit,
    register: (SessionState => Callback) => Unit
  ) {
    def render = Component(this)
  }

  @Lenses case class State (current: SessionState)

  val Component = ScalaComponent.builder[Props]("Arcomage")
    .initialState(State(SessionState.Initial))
    .renderBackend[Backend]
    .componentWillMount(i => i.backend.connectState(i.props))
    .build

  class Backend($: BackendScope[Props, State]) {
    def connectState(p: Props): Callback = Callback {
      p.register(ss => $.modState(State.current.set(ss)))
    }

    def render(s: State, p: Props) = {
      <.div(s.toString)
    }
  }
}
