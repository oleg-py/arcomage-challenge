package ac.frontend.states

import ac.frontend.peering.Peer
import ac.frontend.states.AppState.{Defeat => _, Victory => _, _}
import ac.game.cards.Card
import ac.game.flow._
import Notification.{Draw, _}
import ac.frontend.i18n.Lang
import ac.game.player.CardScope
import cats.effect._
import cats.effect.implicits._
import com.olegpy.shironeko.StoreBase
import cats.implicits._
import monocle.macros.GenLens
import scala.concurrent.duration._

import ac.frontend.states.RematchState._
import cats.data.OptionT
import cats.effect.concurrent.Ref

//noinspection TypeAnnotation
trait StoreAlg[F[_]] { this: StoreBase[F] =>
  import implicits._
  val error = Cell(none[String])
  val app   = Cell[AppState](NameEntry)
  val game  = Cell[Progress](Progress.NotStarted)
  val cards = Cell(Vector.empty[Card])
  val me    = Cell(none[User])
  val enemy = Cell(none[User])
  val locale = Cell[Lang](Lang.En)
  val myTurn  = Cell(false)
  val peerConnection = Cell(none[Peer.Connection[F]])
  val rematchState   = Cell[RematchState](NotAsked)

  val peer: F[Peer[F]] = preload(Peer[F]).onError { case e: Exception =>
    // TODO better error types
    error.set(e.getMessage.some)
  }

  val gameEvents = Events.handled[GameMessage] {
    case RematchRequest =>
      rematchState.update {
        case NotAsked => Asked
        case _        => Accepted
      }
    case KeepAlive => unit
    case EngineNotification(Income) =>
      timer.sleep(2.seconds)
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
    case msg => F.delay(println(msg))
  }
  val myTurnIntents = Events[TurnIntent]

  object animate {
    private[this] val cell = Cell(none[AnimatedCard])
    val state = cell.listen
    val animDuration = 2500.millis
    val sleepDelay = 500.millis

    def apply(card: Card, isEnemy: Boolean, isDiscarded: Boolean): F[Unit] =
      cell.set(AnimatedCard(card, isEnemy, isDiscarded).some) *>
      timer.sleep(sleepDelay) *> {
        timer.sleep(animDuration - sleepDelay) *> cell.set(None)
      }.start.void
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
        OptionT(me.get).map(OpponentReady).semiflatMap(send).getOrElseF(unit)
      case _ => unit
    }

  def fail[A](s: String): F[A] =
    error.set(s.some) *> F.raiseError(new Exception(s))

  trait implicits {
    implicit def timer: Timer[F]
    implicit def contextShift: ContextShift[F]
    implicit def concurrent: Concurrent[F] = F
  }
  val implicits: implicits

  def unit = F.unit

}
