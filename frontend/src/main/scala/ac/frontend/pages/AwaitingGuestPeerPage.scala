package ac.frontend.pages

import ac.frontend.components.PlayerDisplay
import ac.frontend.states.AppState.AwaitingGuest
import slinky.core.StatelessComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html._


@react class AwaitingGuestPeerPage extends StatelessComponent {
  type Props = AwaitingGuest
  def render(): ReactElement =
    div(className := "player-box")(
      PlayerDisplay(props.me),
      div(className := "connection-data")(
        span("Share this link with your friend to start a game:"),
        pre(props.connectionLink)
      )
    )
}
