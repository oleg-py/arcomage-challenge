package ac.frontend.actions

import scala.scalajs.js.typedarray.ArrayBuffer

import ac.frontend.{Store, utils}
import ac.frontend.Store.contextShift
import ac.frontend.peering.Peer.Sink1
import ac.frontend.states.AppState._
import ac.frontend.states.{GameMessage, OpponentReady}
import cats.effect.IO
import cats.syntax.apply._
import scala.concurrent.duration._

object connections {
  private val ConnectionKey = "ac_game"

  private def host(me: User): IO[Unit] =
    for {
      peer <- Store.peer
      url  <- utils.currentUrl[IO]
      _    <- Store.app.set(AwaitingGuest(
        s"${url.toString}?$ConnectionKey=${peer.id}", me))
      _    <- peer.incoming
        .evalMap { Function.tupled(establishConnection) }
        .compile.drain.start
      user <- Store.gameEvents.await1 {
        case OpponentReady(other) => other
      }
      _    <- Store.sendRaw(OpponentReady(me).asBytes)
      _    <- Store.app.set(Playing(me, user))
    } yield ()

  private def establishConnection(msgs: fs2.Stream[IO, ArrayBuffer], sink: Sink1[IO, ArrayBuffer]): IO[Unit] = {
    msgs.map(GameMessage.fromBytes).to(Store.gameEvents.emit).compile.drain.start *>
    Store.sendF.set(Some(sink))
  }

  private def connectToUser(id: String, me: User): IO[Unit] =
    for {
      peer <- Store.peer
      _    <- peer.connect(id).flatMap(Function.tupled(establishConnection))
      _    <- Store.app.set(AwaitingHost)
      _    <- Store.timer.sleep(1.second)
      _    <- Store.sendRaw(OpponentReady(me).asBytes)
      user <- Store.gameEvents.await1 {
        case OpponentReady(other) => other
      }

      _    <- Store.app.set(Playing(me, user))
    } yield ()

  def connect(me: User): IO[Unit] =
    for {
      url <- utils.currentUrl[IO]
      key =  utils.parseQueryString(url.search).get(ConnectionKey)
      _   <- key.fold(host(me))(connectToUser(_, me))
    } yield ()
}
