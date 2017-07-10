package ac.game
package cards
import player._

case class Card (
  name: String,
  color: Card.Color,
  worth: Int,
  effect: Card.Fn,
  discardable: Boolean = true
) extends Card.Fn {
  def cost = color.resource * -worth
  override def apply(s: CardScope): CardScope = effect(s).norm
}

object Card {
  val Noop = Card("", Color.Red, 0, identity)

  type Fn = CardScope => CardScope
  sealed trait Color {
    import Color._
    def resource = this match {
      case Red   => Resources(bricks = 1)
      case Blue  => Resources(gems = 1)
      case Green => Resources(recruits = 1)
    }
  }
  object Color {
    case object Red   extends Color
    case object Blue  extends Color
    case object Green extends Color
  }
}
