package ac.frontend.components

import ac.frontend.Store
import ac.frontend.states.{AppState, StoreAlg}
import ac.frontend.facades.AntDesign.Spin
import ac.frontend.i18n._
import ac.frontend.utils.StreamOps
import ac.game.player.TurnMod
import ac.game.player.TurnMod.ForceDiscard
import cats.effect.Timer
import slinky.core.facade.{Fragment, ReactElement}
import slinky.web.html.{className, div, span}
import typings.antdLib.antdLibComponents.SpinProps
import typings.antdLib.antdLibStrings.small
import com.olegpy.shironeko.util.combine

object Notice extends Store.ContainerNoProps {

  case class State(
    myTurn: Boolean,
    isAnimating: Boolean,
    appState: AppState,
    turnMod: Option[TurnMod]
  )


  def subscribe[F[_]: Subscribe]: fs2.Stream[F, State] = {
    val F = StoreAlg[F]
    implicit val timer: Timer[F] = F.currentTimer
    combine[State].from(
      F.myTurn.discrete,
      F.animate.state.map(_.nonEmpty),
      F.app.discrete,
      F.game.discrete.map(_.state.turnMods.headOption)
    ).frameDebounced.cons1(State(
      myTurn = false,
      isAnimating = true,
      AppState.Playing, None
    ))
  }

  def render[F[_]: Render](state: State): ReactElement = withLang { implicit lang =>
    import state._
    val isEndgame = appState match {
      case AppState.Victory | AppState.Defeat | AppState.Draw => true
      case _ => false
    }
    div(className := "turn-status") {
      if (isAnimating || isEndgame) span(className := "empty")("\u00a0")
      else if (myTurn && turnMod.contains(ForceDiscard)) span(className := "my-turn discard")(Tr(
        "Discard a card now...",
        "Сбросьте карту"
      ))
      else if (myTurn) span(className := "my-turn")(
        Tr(
          ".:: Make your choice ::.",
          ".:: Ваш ход ::."
        )
      )
      else Fragment(
        Spin(SpinProps(size = small)),
        span(className := "enemy-turn")(Tr(
          "Your opponent is thinking hard...",
          "Ваш оппонент размышляет над ходом..."
        ))
      )
    }
  }
}
