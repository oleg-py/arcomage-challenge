package ac.game.cards

import scala.language.postfixOps
import scala.util.Random

import monix.eval.Task


class Cards private (
  val hand: Vector[Card],
  source: Stream[Card]
) {
  def drop(n: Int): Cards = new Cards(hand.updated(n, source.head), source.tail)
  def pull(n: Int): (Card, Cards) = (hand(n), drop(n))
}

object Cards {
  // TODO tagless?
  def initial(handSize: Int): Task[Cards] = Task {
    val allCards = Vector(red cards, blue cards, green cards).flatten
    val (hand, source) = Stream
      .continually { Random.shuffle(allCards) }
      .flatten
      .splitAt(handSize)

    new Cards(hand.toVector, source)
  }
}
