package ac.frontend.peering

import cats.effect._
import cats.effect.syntax.effect._
import cats.implicits._
import scala.scalajs.js
import scala.scalajs.js.typedarray.ArrayBuffer

import ac.frontend.utils
import ac.frontend.utils.JSException
import cats.effect.concurrent.Deferred
import fs2.Stream
import fs2.concurrent.Queue
import java.util.concurrent.atomic.AtomicBoolean


class Peer[F[_]] private (
  val id: String,
  jsPeer: PeerJS,
  queue: Queue[F, Peer.Duplex[F, ArrayBuffer]]
)(implicit F: ConcurrentEffect[F]) {

  val incoming: fs2.Stream[F, Peer.Duplex[F, ArrayBuffer]] =
    queue.dequeue

  private[this] def handleConnection(conn: PeerJS.Connection): F[Peer.Duplex[F, ArrayBuffer]] =
    for {
      msgs <- Queue.synchronous[F, ArrayBuffer]
      sink = (msg: ArrayBuffer) => F.delay(conn.send(msg))
      _ <- F.delay { conn.on("data", { data: ArrayBuffer =>
        msgs.enqueue1(data).toIO.unsafeRunAsyncAndForget()
      }) }
      fail <- Deferred[F, Throwable]
      errStream = Stream.eval_(fail.get.map(_.asLeft[Unit]).rethrow)
      _  <- F.async[Unit] { cb =>
        val done = new AtomicBoolean(false)
        conn.on("open", () =>
          if (done.compareAndSet(false, true)) {
            cb(Right(()))
          })
        conn.on("error", (err: js.Error) => {
          val wrapped = new JSException(err)
          if (done.compareAndSet(false, true)) {
            cb(Left(wrapped))
          } else {
            fail.complete(wrapped).attempt.toIO.unsafeRunAsyncAndForget()
          }
        })
      }
    } yield (msgs.dequeue merge errStream, sink)

  jsPeer.on("connection", { conn: PeerJS.Connection =>
    handleConnection(conn)
      .flatMap(queue.enqueue1)
      .toIO
      .unsafeRunAsyncAndForget()
  })

  def connect(id: String): F[Peer.Duplex[F, ArrayBuffer]] = F.suspend {
    val jsConn = jsPeer.connect(id)
    handleConnection(jsConn)
  }
}

object Peer {

  type Sink1[F[_], A] = A => F[Unit]
  type Duplex[F[_], A] = (Stream[F, A], Sink1[F, A])

  def apply[F[_]](implicit F: ConcurrentEffect[F]): F[Peer[F]] =
    for {
      jsp <- F.delay(new PeerJS(js.Dynamic.literal(port = 443, secure = true)))
      id  <- F.async[String] { cb =>
        jsp.on("open", (id: String) => cb(Right(id)))
        jsp.on("error", (err: js.Error) => {
          if (!utils.pageIsReloading()) cb(Left(new Exception(err.toString)))
        })
      }
      conns <- Queue.synchronous[F, Duplex[F, ArrayBuffer]]
      peer  <- F.delay(new Peer(id, jsp, conns))
    } yield peer
}
