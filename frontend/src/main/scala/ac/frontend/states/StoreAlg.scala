package ac.frontend.states

import scala.scalajs.js.typedarray.ArrayBuffer

import ac.frontend.peering.Peer
import ac.frontend.states.AppState.{NameEntry, User}
import ac.game.cards.Card
import ac.game.flow._
import ac.game.player.CardScope
import cats.effect._
import com.olegpy.shironeko.StoreBase
import cats.syntax.all._

//noinspection TypeAnnotation
trait StoreAlg[F[_]] { this: StoreBase[F] =>
  import implicits._
  val peer: F[Peer[F]] = preload(Peer[F])

  val app   = Cell[AppState](NameEntry)
  val game  = Cell[Progress](Progress.NotStarted)
  val sendF = Cell(none[Peer.Sink1[F, ArrayBuffer]])
  val cards = Cell(Vector.empty[Card])
  val me    = Cell(none[User])
  val enemy = Cell(none[User])

  val gameEvents = Events.handled[GameMessage] {
    case OpponentReady(other) =>
      enemy.set(other.some)
    case ConditionsSet(conds) =>
      game.set(Progress(CardScope(
        conds.initialStats,
        conds.initialStats,
        Vector()
      ), conds.victoryConditions))
    case EngineNotification(CardReceived(card)) =>
      cards.update(_ :+ card)
    case EngineNotification(ResourceUpdate(state)) =>
      game.update(Progress.state.set(state))
    case msg => F.delay(println(msg))
  }
  val myTurnIntents = Events[TurnIntent]


  def send(gm: GameMessage): F[Unit] = {
    sendF.get.flatMap {
      case None => F.delay(println("Connection is not yet established"))
      case Some(f) =>
        println(s"Sending $f (${f.getClass.getName}) to Remote")
        f(gm.asBytes)
    }
  }

  trait implicits {
    implicit def timer: Timer[F]
    implicit def contextShift: ContextShift[F]
    implicit def concurrent: Concurrent[F] = F
  }
  val implicits: implicits
}
