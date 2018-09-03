package ac.webapp.react

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._


case class NameEntry (onName: String => Callback) {
  def /> = NameEntry.Component(this)
}

object NameEntry {
  class Backend($: BackendScope[NameEntry, String]) {
    def setName(e: ReactEventFromInput) =
      Callback { e.persist() } >>
        $.setState(e.target.value)

    def render(p: NameEntry, state: String) =
      <.div(
        <.input(
          ^.tpe := "text",
          ^.value := state,
          ^.onChange ==> setName
        ),
        <.button(
          ^.onClick --> p.onName(state),
          "Set name!"
        )
      )
  }

  val Component = ScalaComponent.builder[NameEntry]("NameEntry")
    .initialState("")
    .renderBackend[Backend]
    .build
}