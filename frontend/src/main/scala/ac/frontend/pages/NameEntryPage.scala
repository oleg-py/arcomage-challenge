package ac.frontend.pages

import ac.frontend.Store
import ac.frontend.actions.connections
import ac.frontend.states.AppState.User
import org.scalajs.dom.raw.{Event, HTMLInputElement}
import slinky.core.Component
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html._


@react class NameEntryPage extends Component {
  type Props = Unit
  case class State(name: String = "", email: String = "")

  def initialState = State()
  def render(): ReactElement = {
    div(className := "player-box")(
      div(className := "avatar-display"),
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
        div(className := "button-container")(
          button(onClick := { _ => Store.dispatch(
            connections.connect(User(
              state.name,
              if (state.email.isEmpty) None else Some(state.email)
            )))}
          )("Enter a game")
        )
      ),
    )
  }
}
