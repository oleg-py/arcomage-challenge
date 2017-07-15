package ac

import cats.MonadError


package object syntax {
  type ErrM[F[_]] = MonadError[F, Throwable]
  def  ErrM[F[_]](implicit instance: ErrM[F]) = instance

  def discard[A](a: A): Unit = ()

  implicit class PipeThroughFunctionSyntax[A](a: A) {
    def |>[B](f: A => B) = f(a)
  }

  def modifyIf[A](cond: A => Boolean, f: A => A)(a: A) = if (cond(a)) f(a) else a
}

