package ac.frontend.actions

import scala.scalajs.js.typedarray.ArrayBuffer

import ac.frontend.peering.Peer.Sink1
import ac.frontend.states.AppState._
import ac.frontend.states._
import cats.syntax.all._
import cats.effect.syntax.all._
import scala.concurrent.duration._

import ac.frontend.utils.query
import ac.game.GameConditions
import ac.game.session.{Registration, Session}

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
        url  <- query.currentUrl[F]
        _    <- Store.me.set(me.some)
        _    <- Store.app.set(AwaitingGuest(
          s"${url.toString}?$ConnectionKey=${peer.id}"))
        _    <- peer.incoming
          .evalMap { Function.tupled(establishConnection) }
          .compile.drain.start
        user <- Store.gameEvents.await1 {
          case OpponentReady(other) => other
        }
        reg  <- Registration[F]
        _    <- Session.start(reg).start
        _    <- reg.enlist(new LocalParticipant[F])
        _    <- reg.enlist(new RemoteParticipant[F])

        _    <- Store.send(OpponentReady(me))
        _    <- Store.app.set(Playing)
      } yield ()

    def connectToUser(id: String, me: User): F[Unit] =
      for {
        peer <- Store.peer
        _    <- Store.me.set(me.some)
        _    <- peer.connect(id).flatMap(Function.tupled(establishConnection))
        _    <- Store.app.set(AwaitingHost)
        _    <- timer.sleep(1.second)
        _    <- Store.send(OpponentReady(me))
        _    <- Store.app.set(SupplyingConditions)
        _    <- Store.myTurnIntents.listen
                  .map(RemoteTurnIntent)
                  .evalMap(Store.send)
                  .compile.drain.start
      } yield ()

    for {
      url <- query.currentUrl[F]
      key =  query.parseQueryString(url.search).get(ConnectionKey)
      _   <- key.fold(host(me))(connectToUser(_, me))
    } yield ()
  }

  def supplyConditions[F[_]](gc: GameConditions)(implicit F: StoreAlg[F]): F[Unit] = {
    import F.implicits._
    val cs = ConditionsSet(gc)
    F.gameEvents.emit1(cs) *> F.send(cs) *> F.app.set(Playing)
  }
}
