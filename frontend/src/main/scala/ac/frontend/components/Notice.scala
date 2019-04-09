package ac.frontend.components

import ac.frontend.Store
import ac.frontend.states.{AppState, StoreAlg}
import ac.frontend.facades.AntDesign.Spin
import ac.frontend.utils.{StreamOps, combine}
import ac.game.player.TurnMod
import ac.game.player.TurnMod.ForceDiscard
import monix.eval.Task
import slinky.core.facade.{Fragment, ReactElement}
import slinky.web.html.{className, div, span}
import typings.antdLib.libSpinMod.SpinProps
import cats.implicits._

object Notice extends Store.ContainerNoProps {

  case class State(
    myTurn: Boolean,
    isAnimating: Boolean,
    appState: AppState,
    turnMod: Option[TurnMod]
  )

  def subscribe(implicit F: StoreAlg[Task]): fs2.Stream[Task, State] =
    combine[State].from(
      F.myTurn.listen,
      F.animate.state.map(_.nonEmpty),
      F.app.listen,
      F.game.listen.map(_.state.turnMods.headOption)
    ).frameDebounced.cons1(State(
      myTurn = false,
      isAnimating = true,
      AppState.Playing, None
    ))


  def render(state: State)(implicit F: StoreAlg[Task]): ReactElement = {
    import state._
    val isEndgame = appState match {
      case AppState.Victory | AppState.Defeat | AppState.Draw => true
      case _ => false
    }
    div(className := "turn-status") {
      if (isAnimating || isEndgame) span(className := "empty")("\u00a0")
      else if (myTurn && turnMod.contains(ForceDiscard)) span(className := "my-turn discard")("Discard a card now...")
      else if (myTurn) span(className := "my-turn")(".:: Make your choice ::.")
      else Fragment(
        Spin(SpinProps(size = "small")),
        span(className := "enemy-turn")("Your opponent is thinking hard...")
      )
    }
  }
}
