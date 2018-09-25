package ac.frontend.states

import scala.scalajs.js.typedarray.ArrayBuffer

import ac.frontend.peering.Peer
import ac.frontend.states.AppState.NameEntry
import ac.game.flow.TurnIntent
import cats.effect._
import com.olegpy.shironeko.StoreBase
import cats.syntax.flatMap._

//noinspection TypeAnnotation
trait StoreAlg[F[_]] { this: StoreBase[F] =>
  import implicits._
  val peer: F[Peer[F]] = preload(Peer[F])

  val app   = Cell[AppState](NameEntry)
  val game  = Cell[GameState](GameState.AwaitingConditions)
  val sendF = Cell[Option[Peer.Sink1[F, ArrayBuffer]]](None)

  val gameEvents = Events.handled[GameMessage] {
    case msg => F.delay(println(msg))
  }
  val myTurnIntents = Events[TurnIntent]


  def send(gm: GameMessage): F[Unit] = {
    sendF.get.flatMap {
      case None => F.delay(println("Connection is not yet established"))
      case Some(f) => f(gm.asBytes)
    }
  }

  trait implicits {
    implicit def timer: Timer[F]
    implicit def contextShift: ContextShift[F]
    implicit def concurrent: Concurrent[F] = F
  }
  val implicits: implicits
}
