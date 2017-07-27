package ac

import cats.MonadError


package object syntax {
  type ErrM[F[_]] = MonadError[F, Throwable]
  def  ErrM[F[_]](implicit F: ErrM[F]) = F

  implicit class PassThroughFunctionOps[A](a: A) {
    def thru[B](f: A => B) = f(a)
    def discard(): Unit = ()
    def when(cond: A => Boolean, f: A => A) = if (cond(a)) f(a) else a
  }
}
