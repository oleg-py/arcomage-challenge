package ac.frontend

import ac.frontend.pages._
import ac.frontend.states.AppState
import slinky.core.facade.ReactElement
import slinky.web.html._
import Store.implicits._
import ac.frontend.states.AppState._
import ac.frontend.utils.StreamOps

object App extends Store.Container(
  Store.app.listen
    .withLatestFrom(Store.me.listen)
    .withLatestFrom(Store.enemy.listen)
    .map { case ((a, b), c) => (a, b, c) }
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
          ConditionsSelectPage(me, enemy)
        case (v @ (Playing | Defeat | Victory), _, _) =>
          div(
            GameScreen(),
            if (v == Victory) {
              div(className := "endgame-notice")("You win!")
            } else if (v == Defeat) {
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
