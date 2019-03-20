package ac.frontend.pages

import ac.frontend.{Store, utils}
import ac.frontend.actions.{connect, settings}
import ac.frontend.states.AppState.User
import org.scalajs.dom.raw.{Event, HTMLInputElement}
import slinky.core.Component
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html._
import scala.scalajs.js.Dynamic.literal

import ac.frontend.states.PersistentSettings
import cats.syntax.apply._
import monix.eval.Coeval
import typings.gravatarDashUrlLib.gravatarDashUrlMod.{^ => gravatarUrl}
import typings.gravatarDashUrlLib.gravatarDashUrlMod.GravatarUrlNs.Options


@react class NameEntryPage extends Component {
  type Props = Unit
  case class State(name: String, email: String)

  private val DefaultAvatarType = "monsterid"
  private val DummyName = utils.names.pick()

  private def avatarName = if (state.name.nonEmpty) state.name else DummyName

  private def avatarUrl = {
    if (state.email contains "@") {
      gravatarUrl(state.email, Options(size = 128, default = DefaultAvatarType))
    } else {
      s"https://gravatar.com/avatar/${avatarName.hashCode.toHexString}?size=128&default=$DefaultAvatarType"
    }
  }

  def initialState: State = {
    val s = PersistentSettings[Coeval].readAll.value()
    State(s.name, s.email)
  }

  def render(): ReactElement = {
    div(className := "box")(
      div(className := "avatar-display")(
        img(src := avatarUrl)
      ),
      div(className := "input-container")(
        label(
          div("Nickname"),
          input(
            placeholder := DummyName,
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
              settings.persistUser(state.name, state.email) *>
              connect(User(avatarName, avatarUrl))
          }
          })("Enter a game")
        )
      ),
    )
  }
}
