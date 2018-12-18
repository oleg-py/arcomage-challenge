package ac.game.cards

import scala.language.postfixOps

import ac.game.Randomizer
import cats.Functor
import cats.syntax.functor._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Greater
import shapeless.{Witness => W}

class Cards private (
  val hand: Vector[Card],
  source: Stream[Card]
) {
  def drop(n: Int): Cards = new Cards(hand.updated(n, source.head), source.tail)
  def pull(n: Int): (Card, Cards) = (hand(n), drop(n))
}

object Cards {
  private val MaxDuplicates = 4
  val allCards = Vector(red cards, blue cards, green cards).flatten

  def initial[F[_]: Randomizer: Functor](handSize: Int Refined Greater[W.`4`.T]): F[Cards] =
    Randomizer[F].shuffles(Stream.fill(MaxDuplicates)(allCards).flatten).map { stream =>
      val (hand, source) = stream splitAt handSize.value
      new Cards(hand.toVector, source)
    }
}
