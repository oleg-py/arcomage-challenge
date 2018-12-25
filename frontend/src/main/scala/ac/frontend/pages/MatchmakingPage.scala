package ac.frontend.pages

import ac.frontend.components.PlayerDisplay
import ac.frontend.states.AppState.User
import slinky.core.StatelessComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html.{className, div}


@react class MatchmakingPage extends StatelessComponent {
  case class Props(me: User, enemy: User, children: ReactElement*)

  def render(): ReactElement = {
    div(className := "box wide")(
      PlayerDisplay(props.me),
      props.children,
      PlayerDisplay(props.enemy)
    )
  }
}
