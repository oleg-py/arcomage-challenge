package ac.communication

import cats.instances.tuple._
import cats.syntax.either._
import cats.syntax.bitraverse._
import monix.eval.Task
import monix.cats._
import monix.execution.atomic.AtomicInt


final class Discriminated[Req, Res] (
  raw: Messaging[EitherID[Req, Res]],
  responder: Req => Task[Res]
) {
  import Discriminated._


  def makeOffer: Task[(Offer, Task[PeerRequest[Req, Res]])] =
    raw.makeOffer.map {
      case (offer, channelL) => (offer, channelL.flatMap(generateReplies))
    }

  def connect(offer: Offer): Task[PeerRequest[Req, Res]] =
    raw.connect(offer).flatMap(generateReplies)

  private def generateReplies(channel: Chan2[Req, Res]): Task[PeerRequest[Req, Res]] =
    channel.received
      .collect {
        case Left(tuple) => tuple.bitraverse(Task.pure(_), responder)
      }
      .mapTask { responseL =>
        for {
          response <- responseL
          _        = println(s"Sending response ${response._2} (id ${response._1})")
          _        <- channel send response.asRight
        } yield response
      }
      .foreachL(_ => ())
      .map(_ => new PeerRequest(channel))
}

object Discriminated {
  type Chan2[Req, Res] = Channel[EitherID[Req, Res]]

  final class PeerRequest[Req, Res](wrapped: Chan2[Req, Res]) extends (Req => Task[Res]) {
    private[this] val counter = AtomicInt(1)

    // TODO tagless?
    override def apply(r: Req): Task[Res] = for {
      id       <- Task { counter.getAndIncrement() }
      _        = println(s"Sending request $r (id $id)")
      _        <- wrapped send (id, r).asLeft
      response <- wrapped.received
        .collect { case Right((`id`, response)) => response }
        .headL
    } yield response
  }
}
