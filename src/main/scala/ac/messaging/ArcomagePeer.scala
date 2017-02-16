package ac.messaging

import boopickle.Default._
import monix.execution.Scheduler

import scala.scalajs.js.typedarray._, TypedArrayBufferOps._

class ArcomagePeer (implicit protected val scheduler: Scheduler) extends Peering {
  override type Payload = GameEvent
  override protected type Serialized = ArrayBuffer

  override protected def serialize(p: GameEvent): ArrayBuffer =
    Pickle.intoBytes(p).arrayBuffer()

  override protected def deserialize(s: ArrayBuffer): GameEvent =
    Unpickle[GameEvent].fromBytes(TypedArrayBuffer.wrap(s))
}
