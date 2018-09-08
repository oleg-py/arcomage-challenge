package ac.frontend.peering

import cats.effect._
import cats.effect.syntax.effect._
import cats.implicits._
import scala.concurrent.duration._
import scala.scalajs.js.typedarray.ArrayBuffer

import fs2.Stream
import fs2.concurrent.Queue
import org.scalajs.dom.Blob


class Peer[F[_]] private (
  val id: String,
  js: PeerJS,
  queue: Queue[F, Peer.Duplex[F, ArrayBuffer]]
)(implicit F: ConcurrentEffect[F]) {

  val incoming: fs2.Stream[F, Peer.Duplex[F, ArrayBuffer]] =
    queue.dequeue

  private[this] def handleConnection(conn: PeerJS.Connection): F[Peer.Duplex[F, ArrayBuffer]] =
    for {
      msgs <- Queue.synchronous[F, ArrayBuffer]
      sink = (msg: ArrayBuffer) => F.delay(conn.send(msg))
      _ <- F.delay { conn.on("data", { data =>
        msgs.enqueue1(data).toIO.unsafeRunAsyncAndForget()
      }) }
    } yield (msgs.dequeue, sink)

  js.on("connection", { conn =>
    handleConnection(conn)
      .flatMap(queue.enqueue1)
      .toIO
      .unsafeRunAsyncAndForget()
  })

  def connect(id: String): F[Peer.Duplex[F, ArrayBuffer]] =
    handleConnection(js.connect(id))
}

object Peer {
  type Sink1[F[_], A] = A => F[Unit]
  type Duplex[F[_], A] = (Stream[F, A], Sink1[F, A])

  def apply[F[_]](implicit F: ConcurrentEffect[F], timer: Timer[F]): F[Peer[F]] =
    for {
      js    <- F.delay(new PeerJS)
      _     <- timer.sleep(500.millis).whileM_(F.delay(js.id.isEmpty))
      id    <- F.delay(js.id.get)
      conns <- Queue.synchronous[F, Duplex[F, ArrayBuffer]]
      peer  <- F.delay(new Peer(id, js, conns))
    } yield peer
}
