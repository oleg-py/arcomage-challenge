package ac.game.player

sealed trait TurnMod

object TurnMod {
  case object PlayAgain    extends TurnMod
  case object ForceDiscard extends TurnMod
}
