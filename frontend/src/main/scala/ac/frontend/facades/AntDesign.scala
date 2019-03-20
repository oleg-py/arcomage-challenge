package ac.frontend.facades


import slinky.core.ExternalComponent
import slinky.core.annotations.react
import typings.antdLib.{antdLibComponents => antd}

object AntDesign {
  @react object Tabs extends ExternalComponent {
    type Props = antd.TabsProps
    override val component = antd.Tabs
  }

  @react object TabPane extends ExternalComponent {
    type Props = antd.TabPaneProps
    override val component = antd.TabPane
  }
}
