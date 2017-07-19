package ac.webapp

import ac.interactions.State._
import ac.webapp.PeerCommand._
import ac.webapp.react.HostJoinWindow
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

object RootComponent {
  case class Props (
    onCommand: PeerCommand => Unit,
    register: (RootState => Callback) => Unit
  ) {
    def render = Component(this)
  }


  val Component = ScalaComponent.builder[Props]("Arcomage")
    .initialState(RootState())
    .renderBackend[Backend]
    .componentWillMount(i => i.backend.connectState(i.props))
    .build

  class Backend($: BackendScope[Props, RootState]) {
    def connectState(p: Props): Callback = Callback { p.register($.setState(_)) }

    def render(s: RootState, p: Props) = {
      val app = s.current match {
        case NotInitialized =>
          HostJoinWindow.Props(
            Callback { p.onCommand(Host) },
            offer => Callback { p.onCommand(Join(offer)) }
          ).render()
        case _ => <.div("Not implemented yet")
//        case HostNameEntry =>
//        case WaitingForGuest(myName) =>
//        case AwaitHostConnection =>
//        case GuestNameEntry(enemyName) =>
//        case SelectConditions(myName, enemyName) =>
      }

      <.div(app)
    }
  }
}
