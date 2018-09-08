package ac.frontend

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

import org.scalajs.dom.raw.{Event, HTMLInputElement}
import slinky.core._
import slinky.core.annotations.react
import slinky.web.html._
import org.scalajs.dom.{KeyboardEvent, window}

@JSImport("resources/App.css", JSImport.Default)
@js.native
object AppCSS extends js.Object

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

  private val css = AppCSS

  def render() = {
    div(className := "App")(
      if (props.isGuest) {
        p(s"You are connected as a guest")
      } else if (props.hostKey.nonEmpty) {
        Seq(
          p(s"You are hosting a chat. Use this url to invite a buddy:"),
          pre(window.location.toString + "?ac_game=" + props.hostKey)
        )
      } else {
        p("You are not connected")
        div(
          input(
            placeholder := "Enter connection string here",
            value := state.conn,
            onChange := { e: Event =>
              val target = e.target.asInstanceOf[HTMLInputElement]
              setState(_.copy(conn = target.value))
            }
          ),
          button(
            onClick := { _ => Main.dispatch(Main.connect(state.conn)) }
          )("Connect"),
          button(onClick := { _ => Main.dispatch(Main.host) })(
            "Be a host"
          )
        )
      },

      props.history.map(msg =>
        div(msg)
      ),
      input(
        placeholder := "Enter message here",
        value := state.msg,
        onChange := { e: Event =>
          val target = e.target.asInstanceOf[HTMLInputElement]
          setState(_.copy(msg = target.value)) }
      ),
      button(onClick := { _ => Main.dispatch(Main.send(state.msg)) })(
        "Send"
      ),
    )
  }
}
