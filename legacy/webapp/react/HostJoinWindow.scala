package ac.webapp.react

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._


case class HostJoinWindow (onHost: Callback, onJoin: String => Callback) {
  def /> = HostJoinWindow.Component(this)
}

object HostJoinWindow {
  case class State (offerText: String)

  class Backend ($: BackendScope[HostJoinWindow, State]) {
    def setOfferText(e: ReactEventFromInput) =
      Callback { e.persist() } >>
        $.setState(State(e.target.value))

    def render(p: HostJoinWindow, s: State) = {
      <.div(
        <.button(
          ^.onClick --> p.onHost,
          "Host a game"
        ),
        <.hr(),
        <.input(
          ^.tpe := "text",
          ^.value := s.offerText,
          ^.onChange ==> setOfferText
        ),
        <.button(
          ^.onClick --> p.onJoin(s.offerText),
          "Join a game"
        )
      )
    }
  }

  val Component = ScalaComponent.builder[HostJoinWindow]("HostJoinWindow")
    .initialState(State(""))
    .renderBackend[Backend]
    .build
}
