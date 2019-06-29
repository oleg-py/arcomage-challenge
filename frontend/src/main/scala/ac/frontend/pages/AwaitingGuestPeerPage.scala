package ac.frontend.pages

import ac.frontend.components.PlayerDisplay
import ac.frontend.states.AppState.User
import ac.frontend.utils
import ac.frontend.facades.AntDesign.{Button, Icon, Input}
import ac.frontend.i18n._
import slinky.core.StatelessComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html._
import typings.clipboardLib.clipboardMod.{^ => ClipboardJS}
import typings.clipboardLib.clipboardMod.Options
import typings.antdLib.libMessageMod.^.{default => message}
import mouse.ignore

@react class AwaitingGuestPeerPage extends StatelessComponent {
  case class Props(me: User, connectionLink: String)

  private[this] val btnClass = "copy-btn"
  private[this] var clipboard: Option[ClipboardJS] = None
  private[this] var lastLang: Lang = Lang.En


  override def componentDidMount(): Unit = {
    val c = new ClipboardJS(s".$btnClass", Options(
      text = _ => props.connectionLink
    ))

    c.on("success", _ => ignore(message.success(Tr(
      "Link copied",
      "Ссылка скопирована"
    ) in lastLang, 3.0)))
    clipboard = Some(c)
  }

  override def componentWillUnmount(): Unit = {
    clipboard.foreach(_.destroy())
    clipboard = None
  }

  def render(): ReactElement = withLang { implicit lang =>
    lastLang = lang
    div(className := "box")(
      PlayerDisplay(props.me),
      div(className := "input-container")(
        label(Tr(
          en = "Share this link with your friend to start a game:",
          ru = "Поделитесь ссылкой с другом для входа в игру"
        )),
        Input(
          value = props.connectionLink,
          addonAfter = { Button(className = btnClass)(Icon("copy")): ReactElement }
        ),
        if (utils.inDevelopment()) div(
          a(href := props.connectionLink, target := "_blank")(
            Tr(
              en = "[Test] Open in new tab",
              ru = "[Тест] Открыть в новой вкладке"
            )
          )
        ) else None
      )
    )
  }
}
