package ac.frontend.states

import ac.game.VictoryConditions
import ac.game.player.CardScope


sealed trait GameState

object GameState {
  case class Progress(
    state: CardScope,
    conds: VictoryConditions
  )

  case object AwaitingConditions extends GameState
  case class Playing(state: Progress) extends GameState
  case class Victory(state: Progress) extends GameState
  case class Defeat(state: Progress) extends GameState
}
