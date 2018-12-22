package ac.frontend.actions

import scala.scalajs.js.typedarray.ArrayBuffer

import ac.frontend.peering.Peer.Sink1
import ac.frontend.states.AppState._
import ac.frontend.states._
import cats.syntax.all._
import cats.effect.syntax.all._
import ac.frontend.utils.{JSException, query}
import ac.game.GameConditions
import ac.game.session.{Registration, Session}
import cats.effect.Sync

object connect {
  private val ConnectionKey = "ac_game"

  def isGuest[F[_]: Sync]: F[Boolean] = query.currentUrl[F]
    .map(_.search)
    .map(query.parseQueryString)
    .map(_ contains ConnectionKey)

  private def establishConnection[F[_]](
    msgs: fs2.Stream[F, ArrayBuffer],
    sink: Sink1[F, ArrayBuffer]
  )(implicit Store: StoreAlg[F]): F[Unit] = {
    import Store.implicits._
    msgs.map(GameMessage.fromBytes).to(Store.gameEvents.emit).compile.drain
      .onError {
        case ex: JSException => Store.error.set(ex.toString.some)
      }.start *>
      Store.sendF.set(Some(sink))
  }

  def apply[F[_]](me: User)(implicit Store: StoreAlg[F]): F[Unit] = {
    import Store.implicits._

    def host(me: User): F[Unit] =
      for {
        peer <- Store.peer
        url  <- query.currentUrl[F]
        _    <- Store.me.set(me.some)
        _    <- Store.app.set(AwaitingGuest(
          s"${url.toString}?$ConnectionKey=${peer.id}"))
        _    <- peer.incoming
          .evalMap { Function.tupled(establishConnection[F]) }
          .head.compile.drain
        _    <- Store.send(OpponentReady(me))
        _ <- Store.gameEvents.await1 { case OpponentReady(_) => }
        reg  <- Registration[F]
        _    <- Session.start(reg).start
        _    <- reg.enlist(new LocalParticipant[F])
        _    <- reg.enlist(new RemoteParticipant[F])

        _    <- Store.app.set(AwaitingConditions)
      } yield ()

    def connectToUser(me: User): F[Unit] =
      for {
        _    <- Store.app.set(SupplyingConditions)
        _    <- Store.me.set(me.some)
        _    <- Store.send(OpponentReady(me))
        _    <- Store.myTurnIntents.listen
                  .map(RemoteTurnIntent)
                  .evalMap(Store.send)
                  .compile.drain.start
      } yield ()

    isGuest[F].flatMap(if (_) connectToUser(me) else host(me))
  }

  def preinitIfGuest[F[_]](implicit Store: StoreAlg[F]): F[Unit] = {
    import Store.implicits._
    query.currentUrl[F]
      .map(url => query.parseQueryString(url.search).get(ConnectionKey))
      .flatMap {
        case None => ().pure[F]
        case Some(id) =>
          Store.peer
            .flatMap(_.connect(id))
            .flatMap(Function.tupled(establishConnection[F]))
      }
  }

  def supplyConditions[F[_]](gc: GameConditions)(implicit F: StoreAlg[F]): F[Unit] = {
    import F.implicits._
    val cs = ConditionsSet(gc)
    F.gameEvents.emit1(cs) *> F.send(cs)
  }
}
