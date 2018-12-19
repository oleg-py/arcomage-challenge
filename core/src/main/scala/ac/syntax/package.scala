package ac

import cats.effect.Sync
import cats.{Applicative, MonadError}
import com.github.ghik.silencer.silent


package object syntax {
  implicit class PassThroughFunctionOps[A](a: A) {
    def thru[B](f: A => B) = f(a)
    def discard(): Unit = ()
    def when(cond: A => Boolean, f: A => A) = if (cond(a)) f(a) else a
  }

  def delay[F[_]](implicit F: Sync[F]) = new DelayPartiallyApplied(F)

  @silent class DelayPartiallyApplied[F[_]](private val F: Sync[F]) extends AnyVal {
    def of[A](a: => A): F[A] = F.delay(a)
  }

}
