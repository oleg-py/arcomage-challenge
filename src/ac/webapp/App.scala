package ac.webapp

import ac.game.GameConditions
import ac.interactions.Event.{ConditionsChosen, NameEntered}
import ac.interactions.Result.Evt
import ac.interactions.{Event, State => SessionState}
import ac.interactions.State._
import ac.webapp.PeerCommand._
import ac.webapp.react.{HostJoinWindow, NameEntry, Battlefield}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

case class App(
  onCommand: PeerCommand => Unit,
  register: (App.State => Callback) => Unit
) {
  def /> = App.Component(this)
  def trigger(event: Event) = Callback { onCommand(Run(Evt(event))) }
}

object App {
  type Props = App
  case class State (current: SessionState = NotInitialized, offer: String = "")

  val Component = ScalaComponent.builder[App]("Arcomage")
    .initialState(State())
    .renderBackend[Backend]
    .componentWillMount(i => i.backend.connectState(i.props))
    .build

  class Backend($: BackendScope[Props, State]) {
    def connectState(p: Props): Callback = Callback { p.register($.setState(_)) }

    def render(s: State, pr: Props) = {
      val app: TagMod = s.current match {
        case NotInitialized =>
          HostJoinWindow(
            Callback { pr.onCommand(Host) },
            offer => Callback { pr.onCommand(Join(offer)) }
          )./>

        case HostNameEntry =>
          <.div(
            <.span(s"Your offer is `${s.offer}`"),
            NameEntry(
              text => pr.trigger(NameEntered(text))
            )./>
          )
        case WaitingForGuest(myName) =>
          <.div(
            s"Please wait for your enemy, $myName..."
          )
        case AwaitHostConnection =>
          <.div(
            "Waiting for host to connect..."
          )
        case GuestNameEntry(enemyName) =>
          <.div(
            s"Fighting against $enemyName",
            NameEntry(
              text => pr.trigger(NameEntered(text))
            )./>
          )
        case SelectConditions(myName, enemyName) =>
          <.div(
            s"$myName is battling against $enemyName",
            <.button(
              ^.onClick --> pr.trigger(ConditionsChosen(GameConditions.testing)),
              "Next"
            )
          )

        case PlayerTurn(p) =>
          Battlefield(p, pr.trigger, isEnemy = false)./>
        case EnemyTurn(p) =>
          Battlefield(p, pr.trigger, isEnemy = true)./>
        case Victory(_) =>
          <.div("You're winner!")
        case Defeat(_) =>
          <.div("You're lose")
      }

      <.div(app)
    }
  }
}
