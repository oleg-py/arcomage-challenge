package ac.frontend

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, _}
import scala.scalajs.LinkingInfo
import scala.scalajs.js.typedarray.ArrayBuffer

import ac.frontend.peering.{HostGuest, Peer, Serialized}
import cats.effect._
import slinky.web.ReactDOM
import slinky.hot
import org.scalajs.dom
import shironeko.Shironeko
import boopickle.Default._
import cats.implicits._

@JSImport("resources/index.css", JSImport.Default)
@js.native
object IndexCSS extends js.Object


@JSExportTopLevel("entrypoint")
object Main extends Shironeko[IO, AppState](AppState()) with IOApp {
  //noinspection TypeAnnotation
  override val F = ConcurrentEffect[IO]

  private val getPeer = Peer[IO].start.unsafeRunSync().join
  private var currentSend: Peer.Sink1[IO, ArrayBuffer] = _

  def send(msg: String): IO[Unit] =
    IO(currentSend).flatMap(f => f(Serialized.from(msg)))

  def connect(id: String): IO[Unit] =
    for {
      peer <- getPeer
      (incoming, outgoing) <- peer.connect(id)
      _ <- states.update(AppState.isGuest.set(true))
      _ <- IO { currentSend = outgoing }
      _ <- incoming
        .evalMap(b => states.update(AppState.history
          .modify(_ :+ Serialized.get[String](b))))
          .compile.drain

    } yield ()

  def host: IO[Unit] =
    for {
      peer <- getPeer
      _    <- states.update(AppState.hostKey.set(peer.id))
    } yield ()

  def run(args: List[String]): IO[ExitCode] = for {
    _    <- IO(IndexCSS)
    _    <- if (LinkingInfo.developmentMode) IO { hot.initialize() }
            else IO.unit
    root <- IO { dom.document.getElementById("root") }
    guestKey <- HostGuest.guestToken[IO]
    peer <- getPeer
    _    <- peer.incoming.evalMap { case (msgs, sink) =>
      IO(currentSend = sink) *> msgs.evalMap { b =>
        states.update(AppState.history.modify(_ :+ Serialized.get[String](b)))
      }.compile.drain
    }.compile.drain.start

    _ <- states.discrete.evalMap { as =>
      IO { ReactDOM.render(App(as), root) }
    }.compile.drain
  } yield ExitCode.Success

  @JSExport
  def exec(): Unit = this.main(Array())
}
