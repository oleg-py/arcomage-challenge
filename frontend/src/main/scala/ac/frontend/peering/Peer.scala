package ac.frontend.peering

import cats.effect._
import cats.effect.syntax.effect._
import cats.implicits._
import scala.scalajs.js
import scala.scalajs.js.typedarray.ArrayBuffer

import fs2.Stream
import fs2.concurrent.Queue


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

  def connect(id: String): F[Peer.Duplex[F, ArrayBuffer]] = F.suspend {
    val jsConn = js.connect(id)
    handleConnection(jsConn) <* F.async[Unit] { cb =>
      jsConn.on("open", () => cb(Right(())))
      jsConn.on("error", err => cb(Left(new RuntimeException(err.toString))))
    }
  }
}

object Peer {
  type Sink1[F[_], A] = A => F[Unit]
  type Duplex[F[_], A] = (Stream[F, A], Sink1[F, A])

  def apply[F[_]](implicit F: ConcurrentEffect[F]): F[Peer[F]] =
    for {
      js    <- F.delay(new PeerJS(js.Dynamic.literal(port = 443, secure = true)))
      _     <- F.async[Unit] { cb =>
        js.on("open", () => cb(Right(())))
        js.on("error", err => println(err)) // TODO - will error handler work here?
      }
      id    <- F.delay(js.id.get)
      _     <- F.raiseError(new Exception("Peer server is not available"))
                .whenA(id == null)
      conns <- Queue.synchronous[F, Duplex[F, ArrayBuffer]]
      peer  <- F.delay(new Peer(id, js, conns))
    } yield peer
}
