package ac.messaging

import ac.model.State
import boopickle.Default.{Pickle, Unpickle}

import scala.scalajs.js.typedarray._
import boopickle.Default._

sealed trait GameEvent

object GameEvent {
  case class ClientReady   (id: String)   extends GameEvent
  case class NameChanged   (name: String) extends GameEvent
  case class CardPlayed    (card: String) extends GameEvent
  case class StateUpdated  (state: State) extends GameEvent

  def toBytes(pe: GameEvent): ArrayBuffer = new TypedArrayBufferOps(Pickle.intoBytes(pe)).arrayBuffer()
  def fromBytes(bytes: ArrayBuffer): GameEvent = Unpickle[GameEvent].fromBytes(TypedArrayBuffer.wrap(bytes))
}

