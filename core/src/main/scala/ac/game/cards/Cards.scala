package ac.game.cards

import scala.language.postfixOps

import ac.game.Randomizer
import cats.Functor
import cats.syntax.functor._


class Cards private (
  val hand: Vector[Card],
  source: Stream[Card]
) {
  def drop(n: Int): Cards = new Cards(hand.updated(n, source.head), source.tail)
  def pull(n: Int): (Card, Cards) = (hand(n), drop(n))
}

object Cards {
  val allCards = Vector(red cards, blue cards, green cards).flatten

  def initial[F[_]: Randomizer: Functor](handSize: Int): F[Cards] =
    Randomizer[F].shuffles(allCards).map { stream =>
      val (hand, source) = stream splitAt handSize
      new Cards(hand.toVector, source)
    }
}
