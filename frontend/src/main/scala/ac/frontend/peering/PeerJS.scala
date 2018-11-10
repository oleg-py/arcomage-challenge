package ac.frontend.peering

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.typedarray.ArrayBuffer

import com.github.ghik.silencer.silent

@JSImport("peerjs", JSImport.Default)
@js.native
@silent class PeerJS(options: js.Any = js.undefined) extends js.Object {
  def id: js.UndefOr[String] = js.native
  def connect(otherId: String): PeerJS.Connection = js.native
  def on(event: String, fn: js.Function0[Unit]): Unit = js.native
  def on(event: String, fn: js.Function1[PeerJS.Connection, Unit]): Unit = js.native
}

@silent object PeerJS {
  @js.native
  trait Connection extends js.Object {
    def send(data: ArrayBuffer): Unit = js.native
    def on(event: String, fn: js.Function1[ArrayBuffer, Unit]): Unit = js.native
  }
}

