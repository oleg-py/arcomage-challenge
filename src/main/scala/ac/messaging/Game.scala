package ac.messaging

import ac.model.GameConditions
import ac.model.Play._
import ac.model.player.State
import monocle.macros.Lenses

object Game {
  sealed trait Command
  case class SetupConditions  (conds: GameConditions) extends Command
  case class ChangeName  (name: String) extends Command
  case class PlayCard    (card: String) extends Command
  case class DiscardCard (card: String) extends Command

  sealed trait Event
  case class Accepted(c: Command) extends Event
  case class Discarded(c: Command, err: String) extends Event

  @Lenses case class UIState (
    playerName: String,
    enemyName: String,
    game: State
  )

  def runCommand(c: Command): (UIState => UIState) = c match {
    case ChangeName(name)  => UIState.enemyName set name
    case PlayCard(card)    => UIState.game modify playSequence(card)
    case DiscardCard(_)    => UIState.game modify playerIncomeOnTurn
  }
}
