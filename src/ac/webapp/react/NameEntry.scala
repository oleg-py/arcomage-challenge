package ac.webapp.react

import japgolly.scalajs.react._
import vdom.all._

case class NameEntry (onName: String => Callback) {
  def /> = NameEntry.Component(this)()
}

object NameEntry {
  class Backend($: BackendScope[NameEntry, String]) {
    def setName(e: ReactEventFromInput) =
      Callback { e.persist() } >>
        $.setState(e.target.value)

    def render(p: NameEntry, state: String) =
      div(
        input(`type` := "text", value := state, onChange ==> setName),
        button("Set name!", onClick --> p.onName(state))
      )
  }

  val Component = ScalaComponent.builder[NameEntry]("NameEntry")
    .initialState("")
    .renderBackend[Backend]
    .build
}
