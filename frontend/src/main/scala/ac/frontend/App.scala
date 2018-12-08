package ac.frontend

import ac.frontend.pages._
import ac.frontend.states.AppState
import slinky.core.facade.ReactElement
import slinky.web.html._
import Store.implicits._
import ac.frontend.states.AppState.User
import ac.frontend.utils.StreamOps

object App extends Store.Container(
  Store.app.listen
    .withLatestFrom(Store.me.listen)
    .withLatestFrom(Store.enemy.listen)
) {
  def render(a: ((AppState, Option[User]), Option[User])): ReactElement = {
    /*_*/
    div(className := "App")(
      a match {
        case ((_, None), _) =>
          NameEntryPage()
        case ((AppState.AwaitingGuest(link), Some(me)), _) =>
          AwaitingGuestPeerPage(me, link)
        case ((AppState.SupplyingConditions, Some(me)), Some(enemy)) =>
          ConditionsSelectPage(me, enemy)
        case ((v @ (AppState.Playing | AppState.Defeat | AppState.Victory), _), _) =>
          div(
            GameScreen(),
            if (v == AppState.Victory) {
              div(className := "endgame-notice")("You win!")
            } else if (v == AppState.Defeat) {
              div(className := "endgame-notice")("You've lost.")
            } else {
              div()
            }
          )
        case _ =>
          div("<-Spinner->")
      }
    )
  }
}
