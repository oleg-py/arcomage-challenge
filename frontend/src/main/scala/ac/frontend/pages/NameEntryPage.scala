package ac.frontend.pages

import ac.frontend.utils
import ac.frontend.states.PersistentSettings
import org.scalajs.dom.raw.{Event, HTMLInputElement}
import slinky.core.Component
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html._
import monix.eval.Coeval
import typings.gravatarDashUrlLib.gravatarDashUrlMod.{^ => gravatarUrl}
import typings.gravatarDashUrlLib.gravatarDashUrlMod.GravatarUrlNs.Options
import ac.frontend.facades.AntDesign.{Avatar, Button, Icon, Input}
import typings.antdLib.antdLibStrings
import typings.antdLib.libAvatarMod.AvatarProps

@react class NameEntryPage extends Component {
  type Props = (String, String, String) => Unit
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
      Avatar(AvatarProps(size = 128, src = avatarUrl, shape = antdLibStrings.square)),
      div(className := "input-container")(
        Input(
          prefix = Icon("user"),
          placeholder = DummyName,
          value = state.name,
          onChange = { e: Event =>
            val target = e.target.asInstanceOf[HTMLInputElement]
            setState(_.copy(name = target.value))
          }),
        Input(
          prefix = Icon("mail"),
          placeholder = "Email (for gravatar only)",
          value = state.email,
          onChange = { e: Event =>
            val target = e.target.asInstanceOf[HTMLInputElement]
            setState(_.copy(email = target.value))
          }),
        div(className := "button-container-right")(
          Button(
            onClick = () => props(state.name, state.email, avatarUrl)
          )("Enter a game")
        )
      ),
    )
  }
}
