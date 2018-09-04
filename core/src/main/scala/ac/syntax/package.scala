package ac

import cats.{Applicative, MonadError}


package object syntax {
  implicit class BoolIFAMethod(b: Boolean) {
    def ifA[F[_]: Applicative](fa: F[Unit]): F[Unit] =
      if (b) fa else Applicative[F].unit
  }

  implicit class PassThroughFunctionOps[A](a: A) {
    def thru[B](f: A => B) = f(a)
    def discard(): Unit = ()
    def when(cond: A => Boolean, f: A => A) = if (cond(a)) f(a) else a
  }
}
