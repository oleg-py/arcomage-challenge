package ac.messaging.protocol
import monix.eval.Task
import monix.execution.Cancelable
import monix.reactive.Observable
import monix.reactive.OverflowStrategy.Unbounded
import monix.reactive.subjects.PublishSubject
import boopickle.Default._

import scala.scalajs.js
import js.Dynamic.{global, literal, newInstance => jsnew}
import scala.scalajs.js.typedarray._
import TypedArrayBufferOps._

class WebRTCClient[A] private (
  val id: String,
  val connectionRequests: Observable[RawCommunicator[A]],
  val connectFn: String => Task[RawCommunicator[A]]
) extends Client[A] {
  override def connect(id: String): Task[RawCommunicator[A]] = connectFn(id)
}

object WebRTCClient {
  private def connectPeerJS[A](implicit p: Pickler[A]): Task[(js.Dynamic, Observable[RawCommunicator[A]])] = Task.create((sc, done) => {
    val me = jsnew(global.Peer)(literal(key = "19e27tcfy8drt3xr"))
    val subj = PublishSubject[RawCommunicator[A]]()
    me.on("open", () => done.onSuccess((me, subj)))
    me.on("error", (err: js.Dynamic) => done.onError(jsErr(err)))
    me.on("connection", (conn: js.Dynamic) => {
      wire[A](me.id.toString, conn).foreach(subj.onNext)(sc)
    })
    Cancelable.empty
  })

  private def wire[A](id: String, connection: js.Dynamic)(implicit p: Pickler[A]): Task[RawCommunicator[A]] = Task.create{ (sc, done) =>
    val send = (a: A) => { connection.send(Pickle.intoBytes(a).arrayBuffer()); () }
    val received = Observable.create[A](Unbounded)(sub => {
      connection.on("close", () => sub.onComplete)
      connection.on("data", (data: ArrayBuffer) => sub.onNext(Unpickle[A].fromBytes(TypedArrayBuffer.wrap(data))))
      connection.on("error", (err: js.Dynamic) => sub.onError(jsErr(err)))
      Cancelable(() => { connection.close(); () })
    }).share(sc)
    done.onSuccess(Communicator[A, A](send, received))
    Cancelable.empty
  }

  private def jsErr(err: js.Dynamic) = {
    new Exception(s"Peer.js ${err.name} (${err.`type`}): ${err.message}")
  }

  def create[A](implicit p: Pickler[A]): Task[WebRTCClient[A]] =
    connectPeerJS[A].map {
      case (me, incoming) => new WebRTCClient[A](me.id.toString, incoming, id => wire[A](me.id.toString, me.connect(id)))
    }
}
