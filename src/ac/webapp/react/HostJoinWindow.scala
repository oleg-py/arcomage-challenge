package ac.webapp.react

import japgolly.scalajs.react._
import vdom.all._

case class HostJoinWindow (onHost: Callback, onJoin: String => Callback) {
  def /> = HostJoinWindow.Component(this)()
}

object HostJoinWindow {
  case class State (offerText: String)

  class Backend ($: BackendScope[HostJoinWindow, State]) {
    def setOfferText(e: ReactEventFromInput) =
      Callback { e.persist() } >>
        $.setState(State(e.target.value))

    def render(p: HostJoinWindow, s: State) = {
      div(
        button("Host a game", onClick --> p.onHost),
        hr(),
        input(`type` := "text", value := s.offerText, onChange ==> setOfferText),
        button("Join a game", onClick --> p.onJoin(s.offerText))
      )
    }
  }

  val Component = ScalaComponent.builder[HostJoinWindow]("HostJoinWindow")
    .initialState(State(""))
    .renderBackend[Backend]
    .build
}
