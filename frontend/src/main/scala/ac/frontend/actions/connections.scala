package ac.frontend.actions

import scala.scalajs.js.typedarray.ArrayBuffer

import ac.frontend.Store
import ac.frontend.Store.contextShift
import ac.frontend.peering.Peer.Sink1
import ac.frontend.peering.Serialized
import ac.frontend.states.AppState.{AwaitingGuest, User}
import ac.frontend.states.GameMessage
import ac.frontend.utils.boopickleInstances._
import boopickle.Default._
import cats.effect.IO
import cats.syntax.apply._

object connections {
  def host(me: User): IO[Unit] =
    for {
      peer <- Store.peer
      _    <- Store.app.set(AwaitingGuest(peer.id, me))
      _    <- peer.incoming
        .evalMap { Function.tupled(establishConnection) }
        .compile.drain.start
    } yield ()

  private def establishConnection(msgs: fs2.Stream[IO, ArrayBuffer], sink: Sink1[IO, ArrayBuffer]): IO[Unit] = {
    Store.sendF.set(Some(sink)) *>
      Store.gameEvents.notify(msgs.map(Serialized.get[GameMessage]))
  }

  def connect(me: User, id: String): IO[Unit] =
    for {
      peer <- Store.peer
      _ <- peer.connect(id).map(Function.tupled(establishConnection))
    } yield ()
}
