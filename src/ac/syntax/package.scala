package ac

import japgolly.scalajs.react.{Callback, CallbackTo}
import monix.eval.Task
import scala.language.implicitConversions

import cats.MonadError
import monix.execution.Scheduler


package object syntax {
  type ??? = Nothing

  type ErrM[F[_]] = MonadError[F, Throwable]
  def  ErrM[F[_]](implicit instance: ErrM[F]) = instance

  def discard[A](a: A): Unit = ()

  implicit class PipeThroughFunctionSyntax[A](a: A) {
    def |>[B](f: A => B) = f(a)
  }

  implicit class CallbackToTask[A](cb: CallbackTo[A]) {
    def task: Task[A] = Task { cb.attemptTry.runNow() } flatMap Task.fromTry
  }

  implicit def convertUnitTaskToCallback(t: Task[Unit])(implicit s: Scheduler): Callback =
    Callback { t.runAsync }

  def modifyIf[A](cond: A => Boolean, f: A => A)(a: A) = if (cond(a)) f(a) else a
}

