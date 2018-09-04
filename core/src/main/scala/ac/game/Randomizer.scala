package ac.game

import scala.util.Random

import cats.effect.Sync
import simulacrum._


@typeclass trait Randomizer[F[_]] {
  def shuffles[A](v: TraversableOnce[A]): F[Stream[A]]
}

object Randomizer {
  implicit def fromSync[F[_]: Sync]: Randomizer[F] = new Randomizer[F] {
    def shuffles[A](v: TraversableOnce[A]): F[Stream[A]] = Sync[F].delay {
      def exec: Stream[A] = Random.shuffle(v).toStream #::: exec
      exec
    }
  }
}