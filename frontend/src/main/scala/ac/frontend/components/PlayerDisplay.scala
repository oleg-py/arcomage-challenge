package ac.frontend.components

import ac.frontend.facades.AntDesign.{Avatar, Tag}
import ac.frontend.states.AppState.User
import slinky.core.StatelessComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html._
import typings.antd.antdStrings
import typings.antd.antdComponents.{AvatarProps, TagProps}


@react class PlayerDisplay extends StatelessComponent {
  type Props = User

  def render(): ReactElement =
    div(className := "player")(
      Avatar(AvatarProps(size = 128, src = props.avatarUrl, shape = antdStrings.square)),
      Tag(TagProps())(props.name)
    )
}
