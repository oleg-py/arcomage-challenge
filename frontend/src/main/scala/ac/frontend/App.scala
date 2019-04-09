package ac.frontend

import ac.frontend.pages._
import ac.frontend.states.{AppState, StoreAlg}
import slinky.core.facade.ReactElement
import slinky.web.html._
import ac.frontend.actions.{connect, settings}
import ac.frontend.components.EndgameNotice
import ac.frontend.states.AppState._
import ac.frontend.utils.{combine, StreamOps}
import ac.frontend.facades.AntDesign.Spin
import monix.eval.Task
import cats.implicits._

object App extends Store.ContainerNoProps {
  case class State(app: AppState, me: Option[User], enemy: Option[User])

  def subscribe(implicit F: StoreAlg[Task]): fs2.Stream[Task, State] =
    combine[State].from(F.app.listen, F.me.listen, F.enemy.listen).frameDebounced


  def render(state: State)(implicit F: StoreAlg[Task]): ReactElement = {
    div(className := "App")(
      state match {
        case State(_, None, _) =>
          NameEntryPage { (name, email, avatar) => exec {
            settings.persistUser[Task](name, email) *>
            connect[Task](User(name, avatar))
          }}
        case State(AwaitingGuest(link), Some(me), _) =>
          AwaitingGuestPeerPage(me, link)
        case State(SupplyingConditions, Some(me), Some(enemy)) =>
          MatchmakingPage(me, enemy)(
            ConditionsSelectPage(cc => exec {
              settings.persistConditions[Task](_ => cc) *>
              cc.pick().traverse_(connect.supplyConditions[Task])
            }).withKey("csp")
          )
        case State(AwaitingConditions, Some(me), Some(enemy)) =>
          MatchmakingPage(me, enemy)(
            div(key := "waiting-notice", className := "conditions-waiting-notice")(
              Spin("Opponent is supplying conditions...")
            )
          )
        case State(Playing | Defeat | Victory | Draw, _, _) =>
          GameScreen()
        case _ =>
          div(className := "spinner-container")(
            Spin("Loading...")
          )
      },
      EndgameNotice()
    )
  }
}
