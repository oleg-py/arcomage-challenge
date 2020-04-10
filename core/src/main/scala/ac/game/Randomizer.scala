package ac.game

import scala.util.Random

import cats.effect.Sync
import simulacrum._


@typeclass trait Randomizer[F[_]] {
  def shuffles[A](v: Iterable[A]): F[LazyList[A]]
}

object Randomizer {
  implicit def fromSync[F[_]: Sync]: Randomizer[F] = new Randomizer[F] {
    def shuffles[A](v: Iterable[A]): F[LazyList[A]] = Sync[F].delay {
      lazy val exec: LazyList[A] = Random.shuffle(v).to(LazyList) #::: exec
      exec
    }
  }
}