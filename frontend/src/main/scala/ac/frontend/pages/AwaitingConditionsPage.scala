package ac.frontend.pages

import ac.frontend.components.PlayerDisplay
import ac.frontend.states.AppState.User
import slinky.core.StatelessComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html._


@react class AwaitingConditionsPage extends StatelessComponent {
  case class Props(me: User, enemy: User)

  def render(): ReactElement = {
    val Props(me, enemy) = props
    div(className := "box wide")(
      PlayerDisplay(me),
      div(
        span("Opponent is supplying conditions"),
      ),
      PlayerDisplay(enemy)
    )
  }
}
