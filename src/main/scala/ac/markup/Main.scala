package ac.markup
import ac.messaging.ArcomagePeer
import mhtml._
import monix.execution.Scheduler.Implicits._
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

object Main extends JSApp {
  @JSExport
  override def main(): Unit = {
    val hw = <div>Hello, world!</div>
    mount(dom.document.body, hw)

    val peer = new ArcomagePeer
    peer.connectionRequests.flatMap(_.received).foreach(println)
    peer.id.foreach(a => {
      js.Dynamic.global.peer_id = a

      val f: js.Function1[String, Unit] = (id: String) => {
        peer.connect(id).foreach(_.received.foreach(println)); ()
      }
      js.Dynamic.global.connect = f
    })


  }
}
