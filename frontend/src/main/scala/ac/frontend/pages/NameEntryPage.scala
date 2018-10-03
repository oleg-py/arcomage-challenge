package ac.frontend.pages

import ac.frontend.Store
import ac.frontend.actions.connect
import ac.frontend.states.AppState.User
import org.scalajs.dom.raw.{Event, HTMLInputElement}
import slinky.core.Component
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html._
import scala.scalajs.js.Dynamic.literal

import ac.frontend.utils.gravatarUrl


@react class NameEntryPage extends Component {
  type Props = Unit
  case class State(name: String = "", email: String = "")

  private val DefaultAvatarType = "monsterid"

  private def avatarUrl = {
    if (state.email contains "@") {
      gravatarUrl(state.email, literal(size = 128, default = DefaultAvatarType))
    } else {
      s"https://gravatar.com/avatar/${state.name.hashCode.toHexString}?size=128&default=$DefaultAvatarType"
    }
  }

  def initialState = State()
  def render(): ReactElement = {
    div(className := "box")(
      div(className := "avatar-display")(
        img(src := avatarUrl)
      ),
      div(className := "input-container")(
        label(
          div("Nickname"),
          input(
            value := state.name,
            onChange := { e: Event =>
              val target = e.target.asInstanceOf[HTMLInputElement]
              setState(_.copy(name = target.value))
            })
        ),
        label(
          div("Email (optional - for avatar only)"),
          input(
            value := state.email,
            onChange := { e: Event =>
              val target = e.target.asInstanceOf[HTMLInputElement]
              setState(_.copy(email = target.value))
            })
        ),
        div(className := "button-container-right")(
          button(
            className := "button",
            onClick := { _ => Store.execS { implicit alg =>
            connect(User(state.name, avatarUrl))
          }
          })("Enter a game")
        )
      ),
    )
  }
}
