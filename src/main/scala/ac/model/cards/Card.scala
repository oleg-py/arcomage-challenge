package ac.model.cards

import ac.model.Resources
import ac.model.player.State

case class Card (
  name: String,
  color: Card.Color,
  cost: Resources,
  effect: State => State,
  discardable: Boolean = true
) extends (State => State) {
  val costNumber: Int = cost.asSeq.max
  override def apply(s: State): State = effect(s)
}

object Card {
  sealed trait Color
  case object Red   extends Color
  case object Green extends Color
  case object Blue  extends Color
}
