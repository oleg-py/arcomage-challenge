package ac.frontend.peering

import org.scalajs.dom.raw.Blob
import scala.scalajs.js.typedarray._

import boopickle.BufferPool
import boopickle.Default._

import java.nio.ByteBuffer

object Serialized {
  def get[A: Pickler](blob: ArrayBuffer): A = {
    Unpickle[A].fromBytes(TypedArrayBuffer.wrap(blob))
  }

  def from[A: Pickler](a: A): ArrayBuffer = {
    toArrayBuffer(Pickle.intoBytes(a))
  }

  private def toArrayBuffer(bb: ByteBuffer) = {
    val ba = Array.ofDim[Byte](bb.limit())
    bb.get(ba)
    BufferPool.release(bb)
    ba.toTypedArray.buffer
  }
}
