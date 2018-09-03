package ac.game.flow

import ac.game.Resources
import ac.game.cards.{Card, Cards}
import cats.Monad
import eu.timepit.refined.types.numeric.NonNegInt


sealed trait Notification
case object TurnEnd extends Notification
case object Victory extends Notification
case object Defeat  extends Notification
case class EnemyPlayed(card: Card, discarded: Boolean) extends Notification

sealed trait TurnIntent extends Product with Serializable {
  def canPlay(hand: Vector[Card], rsc: Resources[NonNegInt]): Boolean = this match {
    case Discard(idx) =>
      idx.value < hand.length && (hand.forall(!_.discardable) || hand(idx.value).discardable)
    case Play(idx) =>
      idx.value < hand.length && (hand(idx.value).cost all_<= rsc)
  }
}

case class Discard(idx: NonNegInt) extends TurnIntent
case class Play(idx: NonNegInt) extends TurnIntent

trait Participant[F[_]] {
  def getTurn(hand: Vector[Card], rsc: Resources[NonNegInt]): F[TurnIntent]
  def notify(notification: Notification): F[Unit]
}

