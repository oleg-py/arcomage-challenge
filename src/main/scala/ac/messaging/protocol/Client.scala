package ac.messaging.protocol

import monix.eval.Task
import monix.reactive.Observable

trait Client[A] {
  def id: String
  val connectionRequests: Observable[RawCommunicator[A]]
  def connect(id: String): Task[RawCommunicator[A]]
}
