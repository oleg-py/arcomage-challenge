package ac.frontend.components

import ac.frontend.Store
import ac.frontend.states.AppState
import ac.frontend.facades.spinners.ScaleLoader
import ac.frontend.utils.StreamOps
import ac.game.player.TurnMod
import ac.game.player.TurnMod.ForceDiscard
import slinky.core.facade.{Fragment, ReactElement}
import slinky.web.html.{className, div, span}


object Notice extends Store.Container(
  Store.myTurn.listen
    .withLatestFrom(Store.animate.state.map(_.nonEmpty))
    .withLatestFrom(Store.app.listen)
    .withLatestFrom(Store.game.listen.map(_.state.turnMods.headOption))
    .frameDebounced
    .cons1((false, false, AppState.Playing, None))
) {
  def render(a: (Boolean, Boolean, AppState, Option[TurnMod])): ReactElement = {
    val (myTurn, isAnimating, appState, turnMod) = a
    val isEndgame = appState match {
      case AppState.Victory | AppState.Defeat => true
      case _ => false
    }
    div(className := "turn-status") {
      if (isAnimating || isEndgame) span(className := "empty")("\u00a0")
      else if (myTurn && turnMod.contains(ForceDiscard)) span(className := "my-turn discard")("Discard a card now...")
      else if (myTurn) span(className := "my-turn")(".:: Make your choice ::.")
      else Fragment(
        ScaleLoader(10, 3, "2px", 3),
        span(className := "enemy-turn")("Your opponent is thinking hard...")
      )
    }
  }
}
