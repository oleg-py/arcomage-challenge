package ac.frontend.facades


import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

import org.scalajs.dom.Event
import slinky.core.ExternalComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import typings.antdLib.{antdLibComponents => antd}

//noinspection TypeAnnotation
object AntDesign {

  @JSImport("antd/dist/antd.css", JSImport.Default)
  @js.native object CSS extends js.Any

  @react object Tabs extends ExternalComponent {
    type Props = antd.TabsProps
    override val component = antd.Tabs
  }

  @react object TabPane extends ExternalComponent {
    type Props = antd.TabPaneProps
    override val component = antd.TabPane
  }

  @react object Icon extends ExternalComponent {
    type Props = antd.IconProps
    override val component = antd.Icon

    def apply(tpe: String): ReactElement =
      this(antd.IconProps(`type` = tpe))
  }

  @react object Input extends ExternalComponent {
    case class Props(
      onChange: js.UndefOr[Event => Unit] = js.undefined,
      prefix: js.UndefOr[ReactElement] = js.undefined,
      value: js.UndefOr[String] = js.undefined,
      placeholder: js.UndefOr[String] = js.undefined,
      addonAfter: js.UndefOr[ReactElement] = js.undefined,
    )
    override val component = antd.Input
  }

  @react object Button extends ExternalComponent {
    case class Props(
      onClick: js.Function0[Unit] = () => {},
      disabled: Boolean = false,
      `type`: String = "primary",
      className: js.UndefOr[String] = js.undefined
    )
    override val component = antd.Button
  }

  @react object Avatar extends ExternalComponent {
    type Props = antd.AvatarProps
    override val component = antd.Avatar
  }

  @react object Tag extends ExternalComponent {
    type Props = antd.TagProps
    override val component = antd.Tag
  }
}
