package ac.markup

import boopickle.Default._
import ac.messaging.protocol.WebRTCClient
import mhtml._
import monix.execution.Scheduler.Implicits._
import monix.reactive.Observable
import org.scalajs.dom
import scala.concurrent.duration._

import scala.scalajs.js
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import scala.xml.Elem

object Main extends JSApp {

  val hw: Var[Elem] = Var {
    <div>
      Hello, user!
      <button onclick={ () => runPeer() }>Confirm</button>
    </div>
  }

  def runPeer() = {
    for {
      peer <- WebRTCClient.create[String]
    } {
      println("Connected to Peer.js")
      println(s"User id is ${peer.id}")
      if (dom.window.location.search.contains("to=")) {
        val parse = "to=([a-z0-9]+)".r
        val m = parse.findFirstMatchIn(dom.window.location.search).get
        val nextId = m.group(1)
        println(s"Connecting to $nextId")
        peer.connect(nextId).foreach(comm => {
          println("Starting ticking...")
          Observable.interval(5.seconds).map(_.toString).foreach(comm.send)
        })
      } else {
        println("Awaiting connection by share link")
        val connLink = dom.window.location.toString + s"&to=${peer.id}"
        hw := <input value={connLink}/>
        peer.connectionRequests.foreach(comm => {
          println("Communcator generated")
          comm.received.foreach(println)
        })
      }
    }
    ()
  }

  @JSExport
  override def main(): Unit = {
    mount(dom.document.body, hw)

    /*val peer = new ArcomagePeer
    peer.connectionRequests.flatMap(_.received).foreach(println)
    peer.id.foreach(a => {
      js.Dynamic.global.peer_id = a

      val f: js.Function1[String, Unit] = (id: String) => {
        peer.connect(id).foreach(_.received.foreach(println)); ()
      }
      js.Dynamic.global.connect = f
    })*/


  }
}
