package ac.frontend.facades.internal

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport


object tabsJS {
  @JSImport("rc-tabs/Tabs", JSImport.Default)
  @js.native
  object Tabs extends js.Object

  @JSImport("rc-tabs/TabPane", JSImport.Default)
  @js.native
  object TabPane extends js.Object

  @JSImport("rc-tabs/TabContent", JSImport.Default)
  @js.native
  object TabContent extends js.Object

  @JSImport("rc-tabs/InkTabBar", JSImport.Default)
  @js.native
  object InkTabBar extends js.Object
}
