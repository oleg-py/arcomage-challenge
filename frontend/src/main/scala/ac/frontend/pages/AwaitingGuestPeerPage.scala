package ac.frontend.pages

import ac.frontend.components.PlayerDisplay
import ac.frontend.states.AppState.User
import ac.frontend.utils
import ac.frontend.facades.AntDesign.{Button, Icon, Input}
import slinky.core.StatelessComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html._
import typings.clipboardLib.clipboardMod.{namespaced => ClipboardJS}
import typings.clipboardLib.clipboardMod.ClipboardJSNs.Options
import typings.antdLib.libMessageMod.^.{default => message}

import java.util.UUID


@react class AwaitingGuestPeerPage extends StatelessComponent {
  case class Props(me: User, connectionLink: String)

  private[this] val btnClass = "copy-btn"
  private[this] var clipboard: Option[ClipboardJS] = None


  override def componentDidMount(): Unit = {
    val c = new ClipboardJS(s".$btnClass", Options(
      text = _ => props.connectionLink
    ))

    c.on("success", _ => message.success("Link copied!", 3.0))
    clipboard = Some(c)
  }

  override def componentWillUnmount(): Unit = {
    clipboard.foreach(_.destroy())
    clipboard = None
  }

  def render(): ReactElement =
    div(className := "box")(
      PlayerDisplay(props.me),
      div(className := "input-container")(
        label("Share this link with your friend to start a game:"),
        Input(
          value = props.connectionLink,
          addonAfter = { Button(className = btnClass)(Icon("copy")) }
        ),
        if (utils.isDevelopment) div(
          a(href := props.connectionLink, target := "_blank")("[Dev] in new tab")
        ) else None
      )
    )
}
