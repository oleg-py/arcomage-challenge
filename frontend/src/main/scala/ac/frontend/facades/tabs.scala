package ac.frontend.facades

import scala.scalajs.js

import ac.frontend.facades.internal.tabsJS
import org.scalajs.dom.raw.Event
import slinky.core.ExternalComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement


object tabs {
  @react object Tabs extends ExternalComponent {
    case class Props(
      renderTabBar: js.Function0[ReactElement],
      renderTabContent: js.Function0[ReactElement],
      activeKey: js.UndefOr[String] = js.undefined,
      defaultActiveKey: js.UndefOr[String] = js.undefined,
      className: js.UndefOr[String] = js.undefined,
      tabBarPosition: js.UndefOr[String] = js.undefined,
      children: Seq[ReactElement] = Seq(),
    )
    val component = tabsJS.Tabs
  }

  @react object TabPane extends ExternalComponent {
    case class Props(
      tab: js.UndefOr[String] = js.undefined,
      key: js.UndefOr[String] = js.undefined,
      className: js.UndefOr[String] = js.undefined,
      active: js.UndefOr[String] = js.undefined,
      style: js.Any = js.undefined,
      destroyInactiveTabPane: js.UndefOr[Boolean] = js.undefined,
      forceRender: js.UndefOr[Boolean] = js.undefined,
      placeholder: js.UndefOr[ReactElement] = js.undefined,
      rootPrefixCls: js.UndefOr[String] = js.undefined,
      id: js.UndefOr[String] = js.undefined,
      children: Seq[ReactElement] = Seq(),
    )
    val component = tabsJS.TabPane
  }

  @react object TabContent extends ExternalComponent {
    case class Props(
      animated: js.UndefOr[Boolean] = js.undefined,
      animatedWithMargin: js.UndefOr[Boolean] = js.undefined,
      prefixCls: js.UndefOr[String] = js.undefined,
      activeKey: js.UndefOr[String] = js.undefined,
      style: js.Any = js.undefined,
      tabBarPosition: js.UndefOr[String] = js.undefined,
      className: js.UndefOr[String] = js.undefined,
      children: Seq[ReactElement] = Seq()
    )
    val component = tabsJS.TabContent
  }

  @react object InkTabBar extends ExternalComponent {
    case class Props(
      onTabClick: js.Function2[String, Event, Unit] = (_, _) => (),
    )
    val component = tabsJS.InkTabBar
  }
}
