package ac.frontend.components

import ac.frontend.states.AppState.User
import slinky.core.StatelessComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html._


@react class PlayerDisplay extends StatelessComponent {
  type Props = User

  def render(): ReactElement =
    div(className := "player")(
      img(src := props.avatarUrl),
      label(props.name)
    )
}
