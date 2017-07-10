package ac.game

import scala.util.Random

import cats.Id
import simulacrum._


@typeclass trait Randomizer[F[_]] {
  def shuffles[A](v: TraversableOnce[A]): F[Stream[A]]
}

object Randomizer {
  object impure extends Randomizer[Id] {
    override def shuffles[A](v: TraversableOnce[A]): Stream[A] =
      Stream
        .continually { Random.shuffle(v) }
        .flatten
  }
}
