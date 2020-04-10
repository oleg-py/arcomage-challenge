package ac.frontend.pages

import ac.frontend.utils
import ac.frontend.states.PersistentSettings
import org.scalajs.dom.raw.{Event, HTMLInputElement}
import slinky.core.Component
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html._
import monix.eval.Coeval
import typings.gravatar.mod.{url => gravatarUrl}
import typings.gravatar.mod.Options
import ac.frontend.facades.AntDesign.{Avatar, Button, Icons, Input}
import ac.frontend.i18n._
import typings.antd.antdStrings
import typings.antd.antdComponents.AvatarProps

@react class NameEntryPage extends Component {
  type Props = (String, String, String, Boolean) => Unit
  case class State(name: String, email: String)

  private val DefaultAvatarType = "monsterid"
  private val DummyName = utils.names.pick()

  private def hasName = state.name.nonEmpty

  private def avatarName = if (hasName) state.name else DummyName

  private def avatarUrl = {
    if (state.email contains "@") {
      gravatarUrl(state.email, Options(size = "128", default = DefaultAvatarType))
    } else {
      s"https://gravatar.com/avatar/${avatarName.hashCode.toHexString}?size=128&default=$DefaultAvatarType"
    }
  }

  def initialState: State = {
    val s = PersistentSettings[Coeval].readAll.value()
    State(s.name, s.email)
  }

  def render(): ReactElement = withLang { implicit lang =>
    div(className := "box")(
      Avatar(AvatarProps(size = 128, src = avatarUrl, shape = antdStrings.square)),
      div(className := "input-container")(
        Input(
          prefix = Icons.User,
          placeholder = DummyName,
          value = state.name,
          onChange = { e: Event =>
            val target = e.target.asInstanceOf[HTMLInputElement]
            setState(_.copy(name = target.value))
          }),
        Input(
          prefix = Icons.Mail,
          placeholder = Tr(
            en = "Email (for gravatar only)",
            ru = "Email (только для аватарки)",
          ) in lang,
          value = state.email,
          onChange = { e: Event =>
            val target = e.target.asInstanceOf[HTMLInputElement]
            setState(_.copy(email = target.value))
          }),
        div(className := "button-container-right")(
          Button(
            onClick = () => props(avatarName, state.email, avatarUrl, hasName)
          )(Tr(
            en = "Enter a game",
            ru = "Войти в игру"
          ))
        )
      ),
    )
  }
}
