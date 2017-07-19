package ac.webapp.react

import japgolly.scalajs.react._
import vdom.all._

object HostJoinWindow {
  case class Props (onHost: Callback, onJoin: String => Callback) {
    def render = Component(this)
  }

  case class State (offerText: String)

  class Backend ($: BackendScope[Props, State]) {
    def setOfferText(e: ReactEventFromInput) =
      Callback { e.persist() } >>
        $.setState(State(e.target.value))

    def render(p: Props, s: State) = {
      div(
        button("Host a game", onClick --> p.onHost),
        hr(),
        input(`type` := "text", value := s.offerText, onChange ==> setOfferText),
        button("Join a game", onClick --> p.onJoin(s.offerText))
      )
    }
  }

  val Component = ScalaComponent.builder[Props]("HostJoinWindow")
    .initialState(State(""))
    .renderBackend[Backend]
    .build
}
