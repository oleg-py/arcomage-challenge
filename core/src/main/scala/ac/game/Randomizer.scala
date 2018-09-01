package ac.game

import simulacrum._


@typeclass trait Randomizer[F[_]] {
  def shuffles[A](v: TraversableOnce[A]): F[Stream[A]]
}
