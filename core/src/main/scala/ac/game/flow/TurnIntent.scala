package ac.game.flow

import ac.game.Resources
import ac.game.cards.Card
import eu.timepit.refined.types.numeric.NonNegInt


sealed trait TurnIntent extends Product with Serializable {
  import TurnIntent._

  def canPlay(hand: Vector[Card], rsc: Resources[NonNegInt]): Boolean = this match {
    case Discard(idx) =>
      idx.value < hand.length && (hand.forall(!_.discardable) || hand(idx.value).discardable)
    case Play(idx) =>
      idx.value < hand.length && (hand(idx.value).cost all_<= rsc)
  }
}

object TurnIntent {
  case class Play(idx: NonNegInt) extends TurnIntent
  case class Discard(idx: NonNegInt) extends TurnIntent
}
