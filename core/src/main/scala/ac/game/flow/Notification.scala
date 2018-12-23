package ac.game.flow

import ac.game.cards.Card
import ac.game.flow.Notification._
import ac.game.player.CardScope
import eu.timepit.refined.types.numeric.NonNegInt
import shapeless._


sealed trait Notification
case class EndStatus(data: EndStatus.C) {
  import EndStatus.C
  def inverse: EndStatus = data match {
    case Inl(Victory) => EndStatus(Coproduct[C](Defeat))
    case Inr(Inl(Defeat)) => EndStatus(Coproduct[C](Victory))
    case Inr(Inr(Inl(Draw))) => this
    case Inr(Inr(Inr(cnil))) => cnil.impossible
  }

  def asNotification: Notification = data.unify
}

object EndStatus {
  type C = Victory.type :+: Defeat.type :+: Draw.type :+: CNil
}

object Notification {
  case object TurnStart                                       extends Notification
  case object TurnEnd                                         extends Notification
  case object Income                                          extends Notification
  case object GameStart                                       extends Notification
  case object Victory                                         extends Notification
  case object Defeat                                          extends Notification
  case object Draw                                            extends Notification
  case class  ResourceUpdate (state: CardScope)               extends Notification
  case class  HandUpdated    (hand: Vector[Card])             extends Notification
  case class  CardPlayed     (idx: NonNegInt, discarded: Boolean) extends Notification
  case class  EnemyPlayed    (card: Card, discarded: Boolean) extends Notification
}

