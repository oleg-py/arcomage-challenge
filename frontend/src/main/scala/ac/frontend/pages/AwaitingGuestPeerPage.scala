package ac.frontend.pages

import ac.frontend.states.AppState.{AwaitingGuest}
import slinky.core.StatelessComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html.{div, pre, span}


@react class AwaitingGuestPeerPage extends StatelessComponent {
  type Props = AwaitingGuest
  def render(): ReactElement =
    div(
      div(props.me.name),
      span("Share this link with your friend:"),
      pre(props.connectionLink)
    )
}
