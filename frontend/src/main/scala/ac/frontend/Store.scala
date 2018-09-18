package ac.frontend

import scala.scalajs.js.typedarray.ArrayBuffer

import ac.frontend.peering.Peer
import ac.frontend.states._
import ac.frontend.states.AppState.NameEntry
import cats.effect.{ContextShift, IO, Timer}
import com.olegpy.shironeko.StoreBase


//noinspection TypeAnnotation
object Store extends StoreBase[IO](Main.Instance) {
  implicit lazy val timer: Timer[IO] = Main.timer
  implicit lazy val contextShift: ContextShift[IO] = Main.contextShift

  val peer: IO[Peer[IO]] = preload(Peer[IO])

  val app   = Cell[AppState](NameEntry)
  val game  = Cell[GameState](GameState.AwaitingConditions)
  val sendF = Cell[Option[Peer.Sink1[IO, ArrayBuffer]]](None)

  val gameEvents = Events.noHandler[GameMessage]


  def sendRaw(bytes: ArrayBuffer): IO[Unit] = {
    sendF.get.flatMap {
      case None => IO(println("Connection is not yet established"))
      case Some(f) => f(bytes)
    }
  }
}
