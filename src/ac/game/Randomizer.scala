package ac.game

import scala.util.Random

import cats.Id
import monix.eval.Coeval
import simulacrum._


@typeclass trait Randomizer[F[_]] {
  def shuffles[A](v: TraversableOnce[A]): F[Stream[A]]
}

object Randomizer {
  private def randomShuffle[A](r: Random, v: TraversableOnce[A]) =
      Stream.continually { r.shuffle(v) }.flatten

  def system = new Randomizer[Coeval] {
    override def shuffles[A](v: TraversableOnce[A]): Coeval[Stream[A]] =
      Coeval.eval { randomShuffle(Random, v) }
  }

  def pure(seed: Long) = new Randomizer[Id] {
    override def shuffles[A](v: TraversableOnce[A]): Stream[A] =
      randomShuffle(new Random(seed), v)
  }
}
