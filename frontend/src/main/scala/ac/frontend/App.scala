package ac.frontend

import ac.frontend.pages._
import ac.frontend.states.{AppState, StoreAlg}
import slinky.core.facade.ReactElement
import slinky.web.html._
import ac.frontend
import ac.frontend.components.EndgameNotice
import ac.frontend.states.AppState._
import ac.frontend.utils.{StreamOps, combine}
import ac.frontend.facades.AntDesign.Spin
import monix.eval.Task

object App extends Store.ContainerNoProps {
  case class State(app: AppState, me: Option[User], enemy: Option[User])

  def subscribe(implicit F: StoreAlg[Task]): fs2.Stream[Task, State] =
    combine[State].from(F.app.listen, F.me.listen, F.enemy.listen).frameDebounced


  def render(state: State)(implicit F: StoreAlg[Task]): ReactElement = {
    div(className := "App")(
      state match {
        case State(_, None, _) =>
          NameEntryPage()
        case State(AwaitingGuest(link), Some(me), _) =>
          AwaitingGuestPeerPage(me, link)
        case State(SupplyingConditions, Some(me), Some(enemy)) =>
          MatchmakingPage(me, enemy)(
            ConditionsSelectPage().withKey("csp")
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
