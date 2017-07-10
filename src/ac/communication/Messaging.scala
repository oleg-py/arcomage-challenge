package ac.communication

import monix.eval.Task

trait Messaging[A] {
  def makeOffer: Task[(Offer, Task[Channel[A]])]
  def connect(offer: Offer): Task[Channel[A]]
}
