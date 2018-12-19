package ac.frontend.pages

import ac.frontend.Store
import ac.frontend.utils.spinners.ScaleLoader
import ac.frontend.utils.StreamOps
import ac.game.player.TurnMod
import ac.game.player.TurnMod.ForceDiscard
import slinky.core.facade.{Fragment, ReactElement}
import slinky.web.html.{className, div, span}
import scala.language.postfixOps


object Notice extends Store.Container(
  Store.myTurn.listen withLatestFrom
  Store.animate.state.map(_.nonEmpty) withLatestFrom
  Store.game.listen.map(_.state.turnMods.headOption) frameDebounced
) {
  def render(a: (Boolean, Boolean, Option[TurnMod])): ReactElement = {
    val (myTurn, isAnimating, turnMod) = a
    div(className := "turn-status") {
      if (isAnimating) span(className := "empty")("\u00a0")
      else if (myTurn && turnMod.contains(ForceDiscard)) span(className := "my-turn discard")("Discard a card now...")
      else if (myTurn) span(className := "my-turn")(".:: Make your choice ::.")
      else Fragment(
        ScaleLoader(10, 3, "2px", 3),
        span(className := "enemy-turn")("Your opponent is thinking hard...")
      )
    }
  }
}
