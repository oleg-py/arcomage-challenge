package ac.frontend.connections

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

import com.github.ghik.silencer.silent
import org.scalajs.dom.Blob

@JSImport("peerjs", JSImport.Default)
@js.native
@silent class PeerJS extends js.Object {
  def id: js.UndefOr[String] = js.native
  def connect(otherId: String): PeerJS.Connection = js.native
  def on(event: String, fn: js.Function0[Unit]): Unit = js.native
  def on(event: String, fn: js.Function1[PeerJS.Connection, Unit]): Unit = js.native
}

@silent object PeerJS {
  @js.native
  trait Connection extends js.Object {
    def send(data: Blob): Unit = js.native
    def on(event: String, fn: js.Function1[js.Dynamic, Unit]): Unit = js.native
  }
}

