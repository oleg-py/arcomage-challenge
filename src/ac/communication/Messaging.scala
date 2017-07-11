package ac.communication

trait Messaging[O[_], M[_], A] {
  trait Channel {
    def send(a: A): M[Unit]
    val received: O[A]
  }

  def makeOffer: M[(Offer, M[Channel])]
  def connect(offer: Offer): M[Channel]
}
