package ac.webapp

import ac.interactions.{State => SessionState}
import ac.interactions.State._
import ac.webapp.PeerCommand._
import ac.webapp.react.HostJoinWindow
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

case class App(
  onCommand: PeerCommand => Unit,
  register: (App.State => Callback) => Unit
) {
  def /> = App.C(this)
}

object App {
  type Props = App
  case class State (current: SessionState = NotInitialized, offer: String = "")

  val C = ScalaComponent.builder[App]("Arcomage")
    .initialState(State())
    .renderBackend[Backend]
    .componentWillMount(i => i.backend.connectState(i.props))
    .build

  class Backend($: BackendScope[Props, State]) {
    def connectState(p: Props): Callback = Callback { p.register($.setState(_)) }

    def render(s: State, p: Props) = {
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
