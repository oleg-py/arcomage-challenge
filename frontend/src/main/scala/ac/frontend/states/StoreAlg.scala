package ac.frontend.states

import scala.scalajs.js.typedarray.ArrayBuffer

import ac.frontend.peering.Peer
import ac.frontend.states.AppState.{NameEntry, User}
import ac.game.cards.Card
import ac.game.flow._
import Notification._
import ac.frontend.i18n.Lang
import ac.game.player.CardScope
import cats.effect._
import cats.effect.implicits._
import com.olegpy.shironeko.StoreBase
import cats.implicits._
import monocle.macros.GenLens
import scala.concurrent.duration._

//noinspection TypeAnnotation
trait StoreAlg[F[_]] { this: StoreBase[F] =>
  import implicits._
  val error = Cell(none[String])
  val app   = Cell[AppState](NameEntry)
  val game  = Cell[Progress](Progress.NotStarted)
  val sendF = Cell(none[Peer.Sink1[F, ArrayBuffer]])
  val cards = Cell(Vector.empty[Card])
  val me    = Cell(none[User])
  val enemy = Cell(none[User])
  val locale = Cell[Lang](Lang.En)

  val peer: F[Peer[F]] = preload(Peer[F]).onError { case e: Exception =>
    // TODO better error types
    error.set(e.getMessage.some)
  }

  val gameEvents = Events.handled[GameMessage] {
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
    case EngineNotification(CardPlayed(card, discarded)) =>
      cards.update(_.filterNot(_ == card)) *>
      animate(card, isEnemy = false, discarded)
    case EngineNotification(EnemyPlayed(card, discarded)) =>
      animate(card, isEnemy = true, discarded)
    case EngineNotification(Victory) =>
      app.set(AppState.Victory)
    case EngineNotification(Defeat) =>
      app.set(AppState.Defeat)
    case RemoteTurnRequest(hand, rsc) =>
      /*_*/
      cards.set(hand) *> game.update(
        GenLens[Progress](_.state.stats.resources).set(rsc)
      )
      /*_*/
    case msg => F.delay(println(msg))
  }
  val myTurnIntents = Events[TurnIntent]

  object animate {
    private[this] val cell = Cell(none[AnimatedCard])
    val state = cell.listen
    val fadeDuration = 500.millis
    val showDuration = 1500.millis

    def apply(card: Card, isEnemy: Boolean, isDiscarded: Boolean): F[Unit] = {
      import AnimatedCard._
      val List(in, show, out) = List(FadeIn, Show, FadeOut)
        .map(AnimatedCard(_, card, isEnemy, isDiscarded))
        .map(_.some).map(cell.set)

      for {
        _ <- in
        _ <- timer.sleep(fadeDuration)
        _ <- show
        _ <- {
          timer.sleep(showDuration) *>
            out *>
            timer.sleep(fadeDuration) *>
            cell.set(None)
        }.start
      } yield ()
    }
  }


  def send(gm: GameMessage): F[Unit] = {
    sendF.get.flatMap {
      case None => error.set("Attempted to send data without a connection".some)
      case Some(f) => f(gm.asBytes)
    }
  }

  def fail[A](s: String): F[A] =
    error.set(s.some) *> F.raiseError(new Exception(s))

  trait implicits {
    implicit def timer: Timer[F]
    implicit def contextShift: ContextShift[F]
    implicit def concurrent: Concurrent[F] = F
  }
  val implicits: implicits
}
