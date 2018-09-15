package ac.frontend

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

import ac.frontend.pages._
import ac.frontend.states.AppState
import slinky.core._
import slinky.core.annotations.react
import slinky.web.html._

@JSImport("resources/logo.svg", JSImport.Default)
@js.native
object ReactLogo extends js.Object

@react class App extends Component {
  type Props = AppState
  case class State(
    msg: String = "",
    conn: String = ""
  )

  def initialState: State = State()

  def render() = {
    div(className := "App")(
      props match {
        case AppState.NameEntry =>
          NameEntryPage()
        case ag @ AppState.AwaitingGuest(_, _) =>
          AwaitingGuestPeerPage(ag)
        case AppState.AwaitingHost =>
          div("Waiting for host...")
        case AppState.Playing(me, other) =>
          div("TODO")
      }
    )
  }
}
