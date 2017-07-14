package ac.webapp

import scala.util.Random

import ac.communication.{Collectable, UniqueId}
import ac.game.Randomizer
import ac.syntax._
import monix.eval.Task
import monix.execution.atomic.AtomicInt
import monix.reactive.Observable


trait Algebras {
  implicit object TaskToObservable extends Collectable[Observable, Task] {
    override def collectFirstM[A, B](o: Observable[A])(f: PartialFunction[A, B]): Task[B] =
      o.collect(f).headL

    override def collectM_[A, B](o: Observable[A])(f: PartialFunction[A, Task[B]]): Task[Unit] =
      o.collect(f).mapTask(identity).lastL.map(discard)
  }

  implicit object ImpureRandomizer extends Randomizer[Task] {
    override def shuffles[A](v: TraversableOnce[A]): Task[Stream[A]] =
      Task.evalOnce { Stream.continually { Random.shuffle(v) }.flatten }
  }

  implicit object TaskUniqueId extends UniqueId[Task] {
    private val counter = AtomicInt(0)
    override def generate: Task[Int] = Task.eval { counter.incrementAndGet() }
  }
}
