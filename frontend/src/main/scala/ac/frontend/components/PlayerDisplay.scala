package ac.frontend.components

import ac.frontend.facades.AntDesign.{Avatar, Tag}
import ac.frontend.states.AppState.User
import slinky.core.StatelessComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html._
import typings.antdLib.antdLibStrings
import typings.antdLib.antdLibComponents.{AvatarProps, TagProps}


@react class PlayerDisplay extends StatelessComponent {
  type Props = User

  def render(): ReactElement =
    div(className := "player")(
      Avatar(AvatarProps(size = 128, src = props.avatarUrl, shape = antdLibStrings.square)),
      Tag(TagProps())(props.name)
    )
}

object PlayerDisplay