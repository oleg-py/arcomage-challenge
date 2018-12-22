package ac.frontend.peering

import cats.effect._
import cats.implicits._
import scala.scalajs.js
import scala.scalajs.js.typedarray.ArrayBuffer

import ac.frontend.utils
import ac.frontend.utils.JSException
import cats.effect.concurrent.Deferred
import fs2.Stream
import fs2.concurrent.Queue
import utils.EffectOps
import java.util.concurrent.atomic.AtomicBoolean


class Peer[F[_]] private (
  val id: String,
  jsPeer: PeerJS,
  queue: Queue[F, Peer.Connection[F]]
)(implicit F: ConcurrentEffect[F]) {

  val incoming: fs2.Stream[F, Peer.Connection[F]] =
    queue.dequeue

  private[this] def handleConnection(conn: PeerJS.Connection): F[Peer.Connection[F]] =
    for {
      msgs <- Queue.synchronous[F, ArrayBuffer]
      _ <- F.delay { conn.on("data", { data: ArrayBuffer =>
        msgs.enqueue1(data).unsafeRunLater()
      }) }
      fail <- Deferred[F, Throwable]
      close <- Deferred[F, Unit]
      scc = new Peer.Connection[F](
        conn,
        msgs.dequeue merge Stream.eval_(fail.get.map(_.asLeft[Unit]).rethrow),
        close.get,
      )
      _  <- F.async[Unit] { cb =>
        val done = new AtomicBoolean(false)
        conn.on("open", () =>
          if (done.compareAndSet(false, true)) {
            cb(Right(()))
          })
        conn.on("error", (err: js.Error) => {
          val wrapped = JSException(err)
          if (done.compareAndSet(false, true)) {
            cb(Left(wrapped))
          // Crazy PeerJS exceptions
          } else if (err.message == "Connection is not open. You should listen for the `open` event before sending messages.") {
            close.complete(()).attempt.unsafeRunLater()
          } else {
            fail.complete(wrapped).attempt.unsafeRunLater()
          }
        })
      }
    } yield scc

  jsPeer.on("connection", { conn: PeerJS.Connection =>
    handleConnection(conn)
      .flatMap(queue.enqueue1)
      .unsafeRunLater()
  })

  def connect(id: String): F[Peer.Connection[F]] = F.suspend {
    handleConnection(jsPeer.connect(id))
  }
}

object Peer {
  class Connection[F[_]] private[Peer] (
    jsc: PeerJS.Connection,
    val messages: Stream[F, ArrayBuffer],
    val waitForClose: F[Unit]
  )(implicit F: Sync[F]) {
    def send(a: ArrayBuffer): F[Unit] = F.delay(jsc.send(a))
    def close: F[Unit] = F.delay(jsc.close())
  }

  def apply[F[_]](implicit F: ConcurrentEffect[F]): F[Peer[F]] =
    for {
      jsp <- F.delay(new PeerJS(js.Dynamic.literal(port = 443, secure = true)))
      id  <- F.async[String] { cb =>
        jsp.on("open", (id: String) => cb(Right(id)))
        jsp.on("error", (err: js.Error) => {
          if (!utils.pageIsReloading()) cb(Left(new Exception(err.toString)))
        })
      }
      conns <- Queue.synchronous[F, Connection[F]]
      peer  <- F.delay(new Peer(id, jsp, conns))
    } yield peer
}
