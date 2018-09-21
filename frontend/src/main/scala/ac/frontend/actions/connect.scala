package ac.frontend.actions

import scala.scalajs.js.typedarray.ArrayBuffer

import ac.frontend.utils
import ac.frontend.peering.Peer.Sink1
import ac.frontend.states.AppState._
import ac.frontend.states._
import cats.syntax.all._
import cats.effect.syntax.all._
import scala.concurrent.duration._

import ac.game.GameConditions
import ac.game.session.Registration

object connect {
  private val ConnectionKey = "ac_game"

  def apply[F[_]](me: User)(implicit Store: StoreAlg[F]): F[Unit] = {
    import Store.implicits._

    def establishConnection(msgs: fs2.Stream[F, ArrayBuffer], sink: Sink1[F, ArrayBuffer]): F[Unit] = {
      msgs.map(GameMessage.fromBytes).to(Store.gameEvents.emit).compile.drain.start *>
        Store.sendF.set(Some(sink))
    }

    def host(me: User): F[Unit] =
      for {
        peer <- Store.peer
        url  <- utils.currentUrl[F]
        _    <- Store.app.set(AwaitingGuest(
          s"${url.toString}?$ConnectionKey=${peer.id}", me))
        _    <- peer.incoming
          .evalMap { Function.tupled(establishConnection) }
          .compile.drain.start
        user <- Store.gameEvents.await1 {
          case OpponentReady(other) => other
        }
        reg  <- Registration[F]

        _    <- Store.sendRaw(OpponentReady(me).asBytes)
        _    <- Store.app.set(Playing(me, user))

        cds  <- Store.gameEvents.await1 {
          case ConditionsSet(conds) => conds
        }
      } yield ()

    def connectToUser(id: String, me: User): F[Unit] =
      for {
        peer <- Store.peer
        _    <- peer.connect(id).flatMap(Function.tupled(establishConnection))
        _    <- Store.app.set(AwaitingHost)
        _    <- timer.sleep(1.second)
        _    <- Store.sendRaw(OpponentReady(me).asBytes)
        user <- Store.gameEvents.await1 {
          case OpponentReady(other) => other
        }

        _    <- Store.app.set(Playing(me, user))
      } yield ()

    for {
      url <- utils.currentUrl[F]
      key =  utils.parseQueryString(url.search).get(ConnectionKey)
      _   <- key.fold(host(me))(connectToUser(_, me))
    } yield ()
  }

  def supplyConditions[F[_]](gc: GameConditions)(implicit F: StoreAlg[F]): F[Unit] = {
    F.gameEvents.emit1(ConditionsSet(gc))
  }
}
