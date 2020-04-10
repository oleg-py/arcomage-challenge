package ac.frontend.facades


import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.|

import scala.language.dynamics
import org.scalajs.dom.Event
import slinky.core.{ExternalComponent, TagMod}
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import typings.antd.{antdComponents => antd}
import typings.antd.antdStrings._
import typings.antd.gridColMod.ColProps
import typings.antd.gridRowMod.RowProps
import typings.antd.radioInterfaceMod.RadioGroupProps
import typings.antd.radioMod.{Group => AntdRadioGroup}

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

  object Icons {
    import typings.react.ScalableSlinky._

    @JSImport("@ant-design/icons", JSImport.Namespace)
    @js.native object Iconpack extends js.Any

    private[this] object I extends Dynamic {
      def selectDynamic(name: String): ExternalComponentP[Unit] =
        new ExternalComponentP[Unit] {
          override val component =
            Iconpack.asInstanceOf[js.Dynamic].selectDynamic(name).asInstanceOf[js.Object]
        }
    }

    def User: ReactElement = I.UserOutlined.apply(())
    def Mail: ReactElement = I.MailOutlined.apply(())
    def Copy: ReactElement = I.CopyOutlined.apply(())
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

  @react object Text extends ExternalComponent {
    type Props = antd.TextProps
    override val component = antd.Text
    def apply(text: String): ReactElement =
      this(antd.TextProps())(text)
  }

  @react object Spin extends ExternalComponent {
    type Props = antd.SpinProps
    override val component = antd.Spin
    def apply(text: String): ReactElement =
      this(antd.SpinProps(size = large, tip = text))
  }

  @react object Select extends ExternalComponent {
    case class Props(
      value: String,
      onChange: String => Unit,
      className: js.UndefOr[String] = js.undefined
    )
    override val component = antd.Select[String]
  }

  @react object Option extends ExternalComponent {
    override val component = typings.antd.mod.Select.Option.asInstanceOf[js.Object]
    case class Props(value: String)

    def apply(key: String, text: String): ReactElement =
      this(Props(key))(text).withKey(key)
  }

  @react object Col extends ExternalComponent {
    type Props = antd.ColProps
    override val component = antd.Col
    def span(span: Int) = this(ColProps(span = span))
  }

  @react object Row extends ExternalComponent {
    type Props = antd.RowProps
    override val component = antd.Row
    def of(tm: TagMod[Nothing]*) = this(RowProps())(tm: _*)
  }

  @react object Slider extends ExternalComponent {
    type Props = antd.SliderProps
    override val component = antd.Slider
  }

  @react object InputNumber extends ExternalComponent {
    type Props = antd.InputNumberProps
    override val component = antd.InputNumber
  }

  @react object Radio extends ExternalComponent {
    type Props = antd.RadioProps
    override val component = antd.Radio

    @react object Button extends ExternalComponent {
      case class Props(value: String | Int)
      override val component = antd.RadioButton

      def of(value: String | Int) = this(Props(value))
    }

    @react object Group extends ExternalComponent {
      type Props = RadioGroupProps
      override val component = AntdRadioGroup
    }
  }
}
