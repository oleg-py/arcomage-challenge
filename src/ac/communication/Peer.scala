package ac.communication

import cats.Monad
import cats.instances.tuple._
import cats.syntax.either._
import cats.syntax.flatMap._
import cats.syntax.functor._

final class Peer[O[_], M[_], Req, Res] (
  responder: Req => M[Res],
  raw: Protocol[O, M, EitherId[Req, Res]]
)(implicit
  M: Monad[M],
  link: Collectable[O, M],
  UniqueId: UniqueId[M]
) {
  class PeerRequest (wrapped: raw.Channel) extends (Req => M[Res]) {
    override def apply(r: Req): M[Res] = for {
      id       <- UniqueId.generate
      _        <- wrapped send (id, r).asLeft
      response <- link.collectFirstM(wrapped.received) {
        case Right((`id`, response)) => response
      }
    } yield response
  }

  def makeOffer: M[(Offer, M[PeerRequest])] =
    raw.makeOffer.map { _.map(_ >>= generateReplies) }

  def connect(offer: Offer): M[PeerRequest] =
    raw.connect(offer) >>= generateReplies

  private def generateReplies(channel: raw.Channel): M[PeerRequest] =
    link
      .collectM_(channel.received) {
        case Left((id, request)) => for {
          response <- responder(request)
          _        <- channel send (id, response).asRight
        } yield ()
      }
      .as(new PeerRequest(channel))
}

object Peer {
  type T[M[_], Req, Res] = Peer[_, M, Req, Res]
}
