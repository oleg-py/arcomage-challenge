package ac.messaging.protocol

import monix.reactive.Observable

case class Communicator[A, B] (
  send: A => Unit,
  received: Observable[B]
)
