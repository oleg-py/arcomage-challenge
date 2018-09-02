package ac.game
package cards

import eu.timepit.refined.auto._
import eu.timepit.refined.types.numeric.NonNegInt
import player._

case class Card (
  name: String,
  color: Card.Color,
  worth: NonNegInt,
  effect: Card.Fn,
  discardable: Boolean = true
) extends Card.Fn {
  def cost: Resources[Int] = color.resource * -worth.value
  override def apply(s: CardScope): CardScope = effect(s)
}

object Card {
  type Fn = CardScope => CardScope

  val Noop = Card("", Color.Red, NonNegInt(0), identity)

  sealed trait Color {
    import Color._
    def resource: Resources[NonNegInt] = this match {
      case Red   => Resources(1, 0, 0)
      case Blue  => Resources(0, 1, 0)
      case Green => Resources(0, 0, 1)
    }
  }
  object Color {
    case object Red   extends Color
    case object Blue  extends Color
    case object Green extends Color
  }
}
