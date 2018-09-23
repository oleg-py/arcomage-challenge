package ac.frontend.pages

import ac.frontend.components.PlayerDisplay
import ac.frontend.states.AppState
import slinky.core.StatelessComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html._


@react class AwaitingConditionsPage extends StatelessComponent {
  type Props = AppState.Playing

  def render(): ReactElement = {
    val AppState.Playing(me, guest) = props
    div(className := "box wide")(
      PlayerDisplay(me),
      div(
        span("Opponent is supplying conditions"),
      ),
      PlayerDisplay(guest)
    )
  }
}
