package ac.model

import shapeless._

case class Card (
  name: String,
  color: Card.Color,
  cost: Resources,
  effect: State => State,
  discardable: Boolean = true
) {
  /*_*/
  val costNumber: Int = Generic[Resources].to(cost).toList.max
}

object Card {
  sealed trait Color
  case object Red extends Color
  case object Green extends Color
  case object Blue extends Color
}
