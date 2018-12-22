package ac.frontend

import ac.frontend.pages._
import ac.frontend.states.AppState
import slinky.core.facade.ReactElement
import slinky.web.html._
import Store.implicits._
import ac.frontend.components.EndgameNotice
import ac.frontend.states.AppState._
import ac.frontend.utils.StreamOps
import ac.frontend.utils.spinners._

object App extends Store.Container(
  Store.app.listen
    .withLatestFrom(Store.me.listen)
    .withLatestFrom(Store.enemy.listen)
    .frameDebounced
) {
  def render(a: (AppState, Option[User], Option[User])): ReactElement = {
    /*_*/
    div(className := "App")(
      a match {
        case (_, None, _) =>
          NameEntryPage()
        case (AwaitingGuest(link), Some(me), _) =>
          AwaitingGuestPeerPage(me, link)
        case (SupplyingConditions, Some(me), Some(enemy)) =>
          MatchmakingPage(me, enemy)(
            ConditionsSelectPage()
          )
        case (AwaitingConditions, Some(me), Some(enemy)) =>
          MatchmakingPage(me, enemy)(
            div(className := "conditions-waiting-notice")(
              div(className := "spinner")(CircleLoader(64)),
              div("Waiting for opponent to supply conditions")
            )
          )
        case (v @ (Playing | Defeat | Victory), _, _) =>
            GameScreen()
        case _ =>
          div(className := "spinner-container")(
            CircleLoader(64)
          )
      },
      EndgameNotice()
    )
  }
}
