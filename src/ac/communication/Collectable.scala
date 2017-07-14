package ac.communication

import monix.eval.Task
import monix.reactive.Observable

trait Collectable[O[_], M[_]] {
  def collectFirstM[A, B](o: O[A])(f: PartialFunction[A, B]): M[B]
  def collectM_[A, B]   (o: O[A])(f: PartialFunction[A, M[B]]): M[Unit]
}

object Collectable {
  trait TaskToObservable extends Collectable[Observable, Task] {
    private def noop(a: Any): Unit = ()

    override def collectFirstM[A, B](o: Observable[A])(f: PartialFunction[A, B]): Task[B] =
      o.collect(f).headL

    override def collectM_[A, B](o: Observable[A])(f: PartialFunction[A, Task[B]]): Task[Unit] =
      Task.deferAction { implicit s =>
        o.collect(f).mapTask(identity).foreach(noop)
        Task.unit
      }
  }
}
