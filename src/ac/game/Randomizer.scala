package ac.game

import scala.util.Random

import cats.Id
import monix.eval.Task
import simulacrum._


@typeclass trait Randomizer[F[_]] {
  def shuffles[A](v: TraversableOnce[A]): F[Stream[A]]
}

object Randomizer {
  trait Impure extends Randomizer[Task] {
    override def shuffles[A](v: TraversableOnce[A]): Task[Stream[A]] =
      Task.eval { randomShuffle(Random, v) }
  }

  trait Pure extends Randomizer[Id] {
    val randomizerSeed: Long

    override def shuffles[A](v: TraversableOnce[A]): Id[Stream[A]] =
      randomShuffle(new Random(randomizerSeed), v)
  }

  private def randomShuffle[A](r: Random, v: TraversableOnce[A]) =
      Stream.continually { r.shuffle(v) }.flatten
}
