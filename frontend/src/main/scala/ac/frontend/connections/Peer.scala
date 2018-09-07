package ac.frontend.connections

import cats.effect._
import cats.effect.syntax.effect._
import cats.implicits._
import scala.concurrent.duration._

import fs2.{Sink, Stream}
import fs2.concurrent.Queue
import org.scalajs.dom.Blob


class Peer[F[_]] private (
  val id: String,
  js: PeerJS,
  queue: Queue[F, Peer.Duplex[F, Blob]]
)(implicit F: ConcurrentEffect[F]) {

  val incoming: fs2.Stream[F, Peer.Duplex[F, Blob]] =
    queue.dequeue

  private[this] def handleConnection(conn: PeerJS.Connection): F[Peer.Duplex[F, Blob]] =
    for {
      msgs <- Queue.synchronous[F, Blob]
      sink = fs2.Sink[F, Blob](msg => F.delay(conn.send(msg)))
      _ <- F.delay { conn.on("data", { data =>
        msgs.enqueue1(data.asInstanceOf[Blob])
          .toIO.unsafeRunAsyncAndForget()
      }) }
    } yield (msgs.dequeue, sink)

  js.on("connection", { conn =>
    handleConnection(conn)
      .flatMap(queue.enqueue1)
      .toIO
      .unsafeRunAsyncAndForget()
  })

  def connect(id: String): F[Peer.Duplex[F, Blob]] =
    handleConnection(js.connect(id))
}

object Peer {
  type Duplex[F[_], A] = (Stream[F, A], Sink[F, A])

  def apply[F[_]](implicit F: ConcurrentEffect[F], timer: Timer[F]): F[Peer[F]] =
    for {
      js    <- F.delay(new PeerJS)
      _     <- timer.sleep(500.millis).whileM_(F.delay(js.id.isEmpty))
      id    <- F.delay(js.id.get)
      conns <- Queue.synchronous[F, Duplex[F, Blob]]
      peer  <- F.delay(new Peer(id, js, conns))
    } yield peer
}
