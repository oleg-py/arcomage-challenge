package ac.communication

import monix.eval.Task
import monix.execution.atomic.AtomicInt
import simulacrum.typeclass


@typeclass trait UniqueId[F[_]] {
  def generate: F[Int]
}

object UniqueId {
  trait ForTask extends UniqueId[Task] {
    private val int = AtomicInt(0)
    override def generate: Task[Int] = Task.eval { int.incrementAndGet() }
  }
}
