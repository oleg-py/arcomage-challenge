package ac.frontend.pages

import ac.frontend.components.PlayerDisplay
import ac.frontend.states.AppState.User
import ac.frontend.utils
import ac.frontend.utils.ClipboardJS
import slinky.core.StatelessComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html._

import java.util.UUID


@react class AwaitingGuestPeerPage extends StatelessComponent {
  case class Props(me: User, connectionLink: String)

  private[this] val btnClass, inputId = "c" + UUID.randomUUID().toString.filterNot(_ == '-')
  private[this] var clipboard: Option[ClipboardJS] = None


  override def componentDidMount(): Unit = {
    clipboard = Some(new ClipboardJS("." + btnClass))
  }

  override def componentWillUnmount(): Unit = {
    clipboard.foreach(_.destroy())
    clipboard = None
  }

  def render(): ReactElement =
    div(className := "box")(
      PlayerDisplay(props.me),
      div(className := "input-container")(
        label(
          div("Share this link with your friend to start a game:"),
          input(id := inputId, value := props.connectionLink, readOnly := true),
        ),
        div(className := "button-container-right")(
          button(
            className := btnClass + " button",
            data-"clipboard-target" := "#" + inputId
          )("Copy")
        ),
        if (utils.isDevelopment) div(
          a(href := props.connectionLink, target := "_blank")("[Dev] in new tab")
        ) else None
      )
    )
}
