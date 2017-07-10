package ac.communication

import monix.eval.Task
import monix.reactive.Observable


trait Channel[A] {
  def send(a: A): Task[Unit]
  val received: Observable[A]
}
