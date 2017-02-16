package ac.messaging

import monix.eval.Task
import monix.execution.Cancelable
import monix.reactive.Observable
import monix.reactive.OverflowStrategy.Unbounded
import monix.reactive.subjects.PublishSubject

import scala.scalajs.js

trait Peering {
  type Payload
  protected type Serialized <: js.Any
  protected def serialize(p: Payload): Serialized
  protected def deserialize(s: Serialized): Payload
  protected def ack(id: String): Payload

  case class Connection (
    send    : Payload => Unit,
    received: Observable[Payload]
  )

  final val id: Task[String] = webRtcPeer.map(_.id.toString)
  private val connectionRequestsS = PublishSubject[Connection]()
  final val connectionRequests: Observable[Connection] = connectionRequestsS.share
  final def connect(peerId: String): Task[Connection] = webRtcPeer map { peer =>
    wire(peer.id.toString, peer.connect(peerId))
  }

  private def wire(id: String, connection: js.Dynamic) = {
    val send = (p: Payload) => { connection.send(serialize(p)); () }
    val received = Observable.create[Payload](Unbounded)(sub => {
      connection.on("open", () => send(ack(id)))
      connection.on("close", () => sub.onComplete())
      connection.on("error", (err: js.Dynamic) => sub.onError(jsException(err)))
      connection.on("data", (data: Serialized) => sub.onNext(deserialize(data)))
      Cancelable.empty
    }).share
    Connection(send, received)
  }

  private def jsException(err: js.Dynamic) = {
    new Exception(s"Peer.js ${err.name} (${err.`type`}): ${err.message}")
  }

  private lazy val webRtcPeer: Task[js.Dynamic] = Task.create[js.Dynamic]((_, done) => {
    val me = js.Dynamic.newInstance(js.Dynamic.global.Peer)(js.Dynamic.literal(key = "19e27tcfy8drt3xr"))
    me.on("open", () => done.onSuccess(me))
    me.on("error", (err: js.Dynamic) => done.onError(jsException(err)))
    me.on("connection", (conn: js.Dynamic) => {
      connectionRequestsS.onNext(wire(me.id.toString, conn))
    })
    Cancelable.empty
  }).memoize
}
