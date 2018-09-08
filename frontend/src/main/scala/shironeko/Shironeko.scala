package shironeko

import cats.effect.ConcurrentEffect
import cats.effect.syntax.effect._
import fs2.concurrent.SignallingRef
import cats.implicits._

abstract class Shironeko[F[_], A](initial: A) {
  implicit protected def F: ConcurrentEffect[F]

  lazy val states: SignallingRef[F, A] =
    SignallingRef[F, A](initial).toIO.unsafeRunSync()

  def modifyF(f: A => F[A]): F[Unit] =
    states.get >>= f >>= states.set

  def dispatch(action: F[Unit]): Unit =
    action.toIO.unsafeRunAsyncAndForget()
}
