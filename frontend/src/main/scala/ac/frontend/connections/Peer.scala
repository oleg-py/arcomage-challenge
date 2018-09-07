package ac.frontend.connections

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import com.github.ghik.silencer.silent

@JSImport("peerjs", JSImport.Default)
@js.native
@silent class Peer extends js.Object {
  def connect(otherId: String): Peer.Connection = js.native
  def on(event: String, fn: js.Function0[Unit]): Unit = js.native
  def on(event: String, fn: js.Function1[js.Dynamic, Unit]): Unit = js.native
}

@silent object Peer {
  @js.native
  trait Connection extends js.Object {
    def on(event: String, fn: js.Function1[js.Dynamic, Unit]): Unit = js.native
  }
}

