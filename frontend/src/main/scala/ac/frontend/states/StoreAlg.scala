package ac.frontend.states

import ac.frontend.states.AppState.{Defeat => _, Victory => _, _}
import ac.game.cards.Card
import ac.game.flow._
import Notification.{Draw, _}
import ac.frontend.i18n.Lang
import ac.game.player.CardScope
import cats.effect._
import cats.effect.implicits._
import com.olegpy.shironeko.StoreDSL
import cats.implicits._
import monocle.macros.GenLens
import scala.concurrent.duration._

import ac.frontend.facades.Peer
import ac.frontend.states.RematchState._
import cats.data.{Chain, OptionT}
import cats.effect.concurrent.Ref
import fs2.Stream

//noinspection TypeAnnotation
class StoreAlg[F[_]](val peer: F[Peer[F]])(
  implicit F: Concurrent[F], timer: Timer[F], dsl: StoreDSL[F]
) {
  import dsl._

  val currentTimer = timer

  val error = cell(none[String])
  val app   = cell[AppState](NameEntry)
  val game  = cell[Progress](Progress.NotStarted)
  val cards = cell(Vector.empty[Card])
  val me    = cell(none[User])
  val enemy = cell(none[User])
  val locale = cell[Lang](Lang.En)
  val myTurn  = cell(false)
  val peerConnection = cell(none[Peer.Connection[F]])
  val rematchState   = cell[RematchState](NotAsked)

  val gameEvents = events[GameMessage]
  val myTurnIntents = events[TurnIntent]

  object animate {
    private[this] val cell = dsl.cell(none[AnimatedCard])
    val state = cell.discrete
    val animDuration = 1500.millis
    val sleepDelay = 500.millis

    def apply(card: Card, isEnemy: Boolean, isDiscarded: Boolean): F[Unit] =
      cell.set(AnimatedCard(card, isEnemy, isDiscarded).some) *>
      timer.sleep(sleepDelay) *> {
        timer.sleep(animDuration - sleepDelay) *> cell.set(None)
      }.start.void
  }

  locally { animate; () } // TODO - objects are lazily initialized, and DSL is dead by its end

  // TODO factor out
  def installHandler: F[Unit] = gameEvents.onNextDo {
    case RematchRequest =>
      rematchState.update {
        case NotAsked => Asked
        case _        => Accepted
      }
    case KeepAlive => ().pure[F]
    case EngineNotification(Income) =>
      Timer[F].sleep(2.seconds)
    case ConnectionRecovery(progressV, cardsV, myTurnV) =>
      app.set(Playing) *>
        game.set(progressV) *> cards.set(cardsV) *> myTurn.set(myTurnV)
    case ConnectionRejected =>
      peerConnection.set(none) *>
        fail("This player is already connected to somebody else")
    case OpponentReady(other) =>
      enemy.set(other.some)
    case ConditionsSet(conds) =>
      game.set(Progress(CardScope(
        conds.initialStats,
        conds.initialStats,
        Vector()
      ), conds.victoryConditions))
    case EngineNotification(GameStart) =>
      app.set(AppState.Playing)
    case EngineNotification(HandUpdated(hand)) =>
      cards.set(hand)
    case EngineNotification(ResourceUpdate(state)) =>
      game.update(Progress.state.set(state))
    case EngineNotification(CardPlayed(idx, discarded)) =>
      for {
        card <- cards.modify(vec => (vec.patch(idx.value, Nil, 1), vec(idx.value)))
        _    <- animate(card, isEnemy = false, discarded)
      } yield ()
    case EngineNotification(EnemyPlayed(card, discarded)) =>
      animate(card, isEnemy = true, discarded)
    case EngineNotification(Victory) =>
      app.set(AppState.Victory)
    case EngineNotification(Defeat) =>
      app.set(AppState.Defeat)
    case EngineNotification(Draw) =>
      app.set(AppState.Draw)
    case EngineNotification(TurnStart) =>
      myTurn.set(true)
    case EngineNotification(TurnEnd) =>
      myTurn.set(false)
    case RemoteTurnRequest(hand, rsc) =>
      cards.set(hand) *> game.update(
        GenLens[Progress](_.state.stats.resources).set(rsc)
      )
    case msg => Sync[F].delay(println(msg))
  }

  val lastEnemyHand = Ref.unsafe[F, Vector[Card]](Vector.empty)

  def send(gm: GameMessage): F[Unit] = {
    peerConnection.get.flatMap {
      case None => error.set("Attempted to send data without a connection".some)
      case Some(conn) => conn.send(gm.asBytes)
    }
  }

  def performRecovery: F[Unit] =
    app.get.flatMap {
      case Playing =>
        (game.get.map(Progress.state.modify(_.reverse)), lastEnemyHand.get, myTurn.get.map(!_))
          .mapN(ConnectionRecovery).flatMap(send) *>
          me.get.flatMap(_.traverse_(usr => send(OpponentReady(usr))))
      case AwaitingConditions =>
        OptionT(me.get).map(OpponentReady).semiflatMap(send).getOrElse(())
      case _ => ().pure[F]
    }

  def fail[A](s: String): F[A] =
    error.set(s.some) *> F.raiseError(new Exception(s))

  def cardHistory: Stream[F, History] =
    animate.state.unNone
      .debounce(animate.animDuration)
      .scan(History(0, Chain.empty, mine = true))(_ >-> _)
}

object StoreAlg {
  def apply[F[_]](implicit ev: StoreAlg[F]): StoreAlg[F] = ev
}