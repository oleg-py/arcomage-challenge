package ac.frontend.facades.internal

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport


object tabsJS {
  @JSImport("rc-tabs/dist/rc-tabs.css", JSImport.Default)
  @js.native
  object Stylesheet extends js.Object

  @JSImport("rc-tabs", JSImport.Default)
  @js.native
  object Tabs extends js.Object

  @JSImport("rc-tabs", "TabPane")
  @js.native
  object TabPane extends js.Object

  @JSImport("rc-tabs/lib/TabContent", JSImport.Default)
  @js.native
  object TabContent extends js.Object

  @JSImport("rc-tabs/lib/InkTabBar", JSImport.Default)
  @js.native
  object InkTabBar extends js.Object
}
