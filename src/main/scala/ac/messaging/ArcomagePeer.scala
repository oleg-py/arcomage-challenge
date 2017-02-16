package ac.messaging

import ac.messaging.GameEvent.ClientReady

import scala.scalajs.js.typedarray._

class ArcomagePeer extends Peering {
  override type Payload = GameEvent
  override protected type Serialized = ArrayBuffer

  override protected def serialize(p: GameEvent): ArrayBuffer = GameEvent.toBytes(p)
  override protected def deserialize(s: ArrayBuffer): GameEvent = GameEvent.fromBytes(s)
  override protected def ack(id: String): GameEvent = ClientReady(id)
}
