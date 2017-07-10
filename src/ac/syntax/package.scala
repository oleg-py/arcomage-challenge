package ac

import japgolly.scalajs.react.{Callback, CallbackTo}
import monix.eval.Task
import scala.language.implicitConversions

import monix.execution.Scheduler


package object syntax {
  type ??? = Nothing

  implicit class PipeThroughFunctionSyntax[A](a: A) {
    def |>[B](f: A => B) = f(a)
  }

  implicit class CallbackToTask[A](cb: CallbackTo[A]) {
    def task: Task[A] = Task { cb.attemptTry.runNow() } flatMap Task.fromTry
  }

  implicit def convertUnitTaskToCallback(t: Task[Unit])(implicit s: Scheduler): Callback =
    Callback { t.runAsync }

  implicit class TaskObjectOps(t: Task.type) {
    def scheduler: Task[Scheduler] = Task.deferAction(Task.pure)
  }

  implicit class ModifyIfSyntax[A](a: A) {
    def modifyIf(cond: A => Boolean, f: A => A) = syntax.modifyIf(cond, f)(a)
  }

  def modifyIf[A](cond: A => Boolean, f: A => A)(a: A) = if (cond(a)) f(a) else a
}

