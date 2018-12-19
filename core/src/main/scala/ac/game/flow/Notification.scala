package ac.game.flow

import ac.game.cards.Card
import ac.game.player.CardScope
import eu.timepit.refined.types.numeric.NonNegInt


sealed trait Notification

object Notification {
  case object TurnStart                                       extends Notification
  case object TurnEnd                                         extends Notification
  case object GameStart                                       extends Notification
  case object Victory                                         extends Notification
  case object Defeat                                          extends Notification
  case class  ResourceUpdate (state: CardScope)               extends Notification
  case class  HandUpdated    (hand: Vector[Card])             extends Notification
  case class  CardPlayed     (idx: NonNegInt, discarded: Boolean) extends Notification
  case class  EnemyPlayed    (card: Card, discarded: Boolean) extends Notification
}

