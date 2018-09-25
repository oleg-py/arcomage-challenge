package ac.frontend.pages

import ac.frontend.components.PlayerDisplay
import ac.frontend.states.AppState.User
import slinky.core.StatelessComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html._


@react class AwaitingGuestPeerPage extends StatelessComponent {
  case class Props(me: User, connectionLink: String)
  def render(): ReactElement =
    div(className := "box")(
      PlayerDisplay(props.me),
      div(className := "connection-data")(
        span("Share this link with your friend to start a game:"),
        pre(props.connectionLink)
      )
    )
}
