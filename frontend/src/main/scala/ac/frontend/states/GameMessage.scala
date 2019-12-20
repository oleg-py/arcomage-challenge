package ac.frontend.states

import scala.scalajs.js.typedarray._

import ac.frontend.states.AppState.User
import ac.game.{GameConditions, Resources}
import ac.game.flow.{Notification, TurnIntent}
import boopickle.BufferPool
import boopickle.Default._
import ac.game.cards.Card
import ac.game.player.Player
import cats.Functor
import eu.timepit.refined.api.Refined
import eu.timepit.refined.types.numeric.NonNegInt
import higherkindness.droste.data.Fix

import java.nio.ByteBuffer


sealed trait GameMessage {
  def asBytes: ArrayBuffer = {
    import GameMessage.boopickleInstances._
    GameMessage.toArrayBuffer(Pickle.intoBytes(this))
  }
}
case class OpponentReady(other: User) extends GameMessage
case class EngineNotification(n: Notification) extends GameMessage
case class ConditionsSet(conds: GameConditions) extends GameMessage
case class RemoteTurnIntent(ti: TurnIntent) extends GameMessage
case class RemoteTurnRequest(cards: Vector[Card], rsc: Resources[NonNegInt]) extends GameMessage
case object ConnectionRejected extends GameMessage
case object KeepAlive extends GameMessage
case object RematchRequest extends GameMessage
case class ConnectionRecovery(progress: Progress, cards: Vector[Card], myTurn: Boolean) extends GameMessage

object GameMessage {
  private object boopickleInstances {
    implicit val cardPickler: Pickler[Card] = generatePickler[Card]
    implicit val playerPickler: Pickler[Player] = generatePickler[Player]

    implicit def refinedType[T: Pickler, P]: Pickler[T Refined P] =
      transformPickler[T Refined P, T](Refined.unsafeApply)(Refined.unapply(_).get)

    implicit def pickleFix[F[_]: Functor](implicit p: Pickler[F[Unit]]): Pickler[Fix[F]] =
      new Pickler[Fix[F]] {
        override def pickle(f: Fix[F])(implicit state: PickleState): Unit = {

          val fields = new collection.mutable.ArrayBuffer[Fix[F]](32)
          val fUnit = Functor[F].map(Fix.un(f)) { a =>
            fields += a
            ()
          }
          p.pickle(fUnit)
          fields.foreach(pickle)

          ()
        }

        override def unpickle(implicit state: UnpickleState): Fix[F] = {
          val fUnit = p.unpickle
          Fix(Functor[F].map(fUnit)(_ => unpickle))
        }
      }
  }

  def fromBytes(ab: ArrayBuffer): GameMessage = {
    import boopickleInstances._
    Unpickle.apply[GameMessage]
      .fromBytes(TypedArrayBuffer.wrap(ab))
  }


  private def toArrayBuffer(bb: ByteBuffer) = {
    val ba = Array.ofDim[Byte](bb.limit())
    bb.get(ba)
    BufferPool.release(bb)
    ba.toTypedArray.buffer
  }
}