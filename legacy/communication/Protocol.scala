package ac.communication

trait Protocol[O[_], M[_], A] {
  trait Channel {
    def send(a: A): M[Unit]
    val received: O[A]
  }

  def makeOffer: M[(String, M[Channel])]
  def connect(offer: String): M[Channel]
}
