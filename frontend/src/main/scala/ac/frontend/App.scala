package ac.frontend

import ac.frontend.pages._
import ac.frontend.states.{AppState, GameState}
import slinky.core.facade.ReactElement
import slinky.web.html._
import Store.implicits._
import ac.frontend.states.GameState.AwaitingConditions

object App extends Store.Container(utils.zipND(Store.app.listen, Store.game.listen)) {
  def render(a: (AppState, GameState)): ReactElement = {
    div(className := "App")(
      a match {
        case (AppState.NameEntry, _) =>
          NameEntryPage()
        case (ag @ AppState.AwaitingGuest(_, _), _)=>
          AwaitingGuestPeerPage(ag)
        case (AppState.AwaitingHost, _) =>
          div("Waiting for host...")
        case (sc @ AppState.SupplyingConditions(_, _), _) =>
          ConditionsSelectPage(sc)
        case (AppState.Playing(_, _), AwaitingConditions) =>
          div("Waiting for conditions")
        case (AppState.Playing(_, _), state) =>
          println(state)
          div("TODO")
      }
    )
  }
}
