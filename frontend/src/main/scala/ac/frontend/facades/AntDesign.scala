package ac.frontend.facades


import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.|

import org.scalajs.dom.Event
import slinky.core.{ExternalComponent, KeyAddingStage, TagMod}
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import typings.antdLib.{antdLibComponents => antd}
import typings.antdLib.antdLibStrings._
import typings.antdLib.esCheckboxCheckboxMod.AbstractCheckboxProps
import typings.antdLib.esGridColMod.ColProps
import typings.antdLib.esGridRowMod.RowProps
import typings.antdLib.libRadioInterfaceMod.RadioGroupProps
import typings.antdLib.libRadioMod.{Group => AntdRadioGroup}

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
    type Props = antd.SelectProps[String]
    override val component = antd.Select[String]
  }

  @react object Option extends ExternalComponent {
    type Props = antd.OptionProps
    override val component = antd.Option

    def apply(key: String, text: String): ReactElement =
      this(antd.OptionProps()).withKey(key)(text)
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
      type Props = antd.RadioButtonProps
      override val component = antd.RadioButton

      def of(value: String | Int) = this(AbstractCheckboxProps(value = value.merge.asInstanceOf[js.Any]))
    }

    @react object Group extends ExternalComponent {
      type Props = RadioGroupProps
      override val component = AntdRadioGroup
    }
  }
}
