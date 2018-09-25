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
    div(className := "App")(
      a match {
        case ((_, None), _) =>
          NameEntryPage()
        case ((AppState.AwaitingGuest(link), Some(me)), _) =>
          AwaitingGuestPeerPage(me, link)
        case ((AppState.AwaitingHost, _), _) =>
          div("Waiting for host...")
        case ((AppState.SupplyingConditions, Some(me)), Some(enemy)) =>
          ConditionsSelectPage(me, enemy)
        case ((AppState.AwaitingConditions, Some(me)), Some(enemy)) =>
          AwaitingConditionsPage(me, enemy)
        case _ =>
          div("TODO")
      }
    )
  }
}
