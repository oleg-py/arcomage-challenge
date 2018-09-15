package ac.frontend.pages

import slinky.core.Component
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html._


@react class NameEntryPage extends Component {
  type Props = Unit
  case class State(name: String = "", email: String = "")

  def initialState = State()
  def render(): ReactElement = {
    div(
      div(className := "avatarDisplay"),
      div(className := "input-container")(
        label(
          span("Nickname"),
          input()
        ),
        label(
          span("Email (optional - for avatar only)"),
          input()
        )
      ),
      button("Enter a game")
    )
  }
}
