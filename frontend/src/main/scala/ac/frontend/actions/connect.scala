package ac.frontend.actions

import ac.frontend.states.AppState._
import ac.frontend.states._
import cats.syntax.all._
import cats.effect.syntax.all._
import ac.frontend.utils.{JSException, query}
import ac.game.GameConditions
import cats.effect.{Concurrent, Sync, Timer}
import fs2._
import scala.concurrent.duration._

import ac.frontend.facades.Peer
import cats.Apply

object connect {
  private val ConnectionKey = "ac_game"

  def isGuest[F[_]: Sync]: F[Boolean] = query.currentUrl[F]
    .map(_.search)
    .map(query.parseQueryString)
    .map(_ contains ConnectionKey)

  private def establishConnection[F[_]: Concurrent](
    conn: Peer.Connection[F]
  )(implicit Store: StoreAlg[F]): F[Unit] = {
    conn.messages.map(GameMessage.fromBytes).through(Store.gameEvents.emit).compile.drain
      .onError {
        case ex: JSException => Store.error.set(ex.toString.some)
      }.start *> Store.peerConnection.set(conn.some) <*
      conn.waitForClose.flatMap(_ => Store.peerConnection.set(none)).start
  }

  def apply[F[_]: Concurrent: Timer](me: User)(implicit Store: StoreAlg[F]): F[Unit] = {
    def host(me: User): F[Unit] =
      for {
        peer <- Store.peer
        url  <- query.currentUrl[F]
        _    <- Store.me.set(me.some)
        _    <- Store.app.set(AwaitingGuest(
          s"${url.toString}?$ConnectionKey=${peer.id}"))
        _    <- peer.incoming.pull.uncons1.flatMap {
          case Some((hd, tl)) =>
            val handleOtherPeers = tl.evalMap { conn =>
              Store.peerConnection.get.flatMap {
                case Some(_) => conn.send(ConnectionRejected.asBytes)
                case None => establishConnection(conn) *>
                  Store.performRecovery
              }
            }.compile.drain.start
            Pull.eval(establishConnection(hd) >> handleOtherPeers.void)
          case None => Pull.raiseError(new RuntimeException("Unexpected end of stream"))
        }.stream.compile.drain
        _    <- Store.send(OpponentReady(me))
        _ <- Store.gameEvents.await1 { case OpponentReady(_) => }
        _    <- matches.begin[F]()
        _    <- Store.app.set(AwaitingConditions)
        _    <- Stream.awakeEvery[F](3.seconds).evalMap { _ =>
          Store.peerConnection.get.map(_.isDefined).ifM(
            Store.send(KeepAlive),
            ().pure[F]
          )
        }.compile.drain.start
      } yield ()

    def connectToUser(me: User): F[Unit] =
      for {
        _    <- Store.app.update {
          case NameEntry => SupplyingConditions
          case other => other
        }
        _    <- Store.me.set(me.some)
        // Wait for connection to arrive
        _    <- Store.peerConnection.listen.unNone
                  .take(1).compile.drain
        _    <- Store.send(OpponentReady(me))
        _    <- Store.myTurnIntents.listen
                  .map(RemoteTurnIntent)
                  .evalMap(Store.send)
                  .compile.drain.start
      } yield ()

    isGuest[F].flatMap(if (_) connectToUser(me) else host(me))
  }

  def preinitIfGuest[F[_]: Concurrent](implicit Store: StoreAlg[F]): F[Unit] = {
    query.currentUrl[F]
      .map(url => query.parseQueryString(url.search).get(ConnectionKey))
      .flatMap {
        case None => ().pure[F]
        case Some(id) =>
          Store.peer
            .flatMap(_.connect(id))
            .flatMap(establishConnection[F])
      }
  }

  def supplyConditions[F[_]: Apply](gc: GameConditions)(implicit F: StoreAlg[F]): F[Unit] = {
    val cs = ConditionsSet(gc)
    F.gameEvents.emit1(cs) *> F.send(cs)
  }
}
