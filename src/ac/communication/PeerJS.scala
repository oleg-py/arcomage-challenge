package ac.communication

import ac.syntax._
import scala.concurrent.duration._
import scala.scalajs.js
import scala.scalajs.js.Dynamic.{literal, global => g, newInstance => jsnew}
import scala.scalajs.js.typedarray._
import scala.util.{Failure, Success}

import boopickle.BufferPool
import boopickle.Default._
import monix.eval.{Callback, Task}
import monix.execution.Cancelable
import monix.reactive.subjects.PublishSubject

import java.nio.ByteBuffer

final class PeerJS[A: Pickler](apiKey: String) extends Messaging[A] {
  private def toArrayBuffer(bb: ByteBuffer) = {
    val ba = Array.ofDim[Byte](bb.limit())
    bb.get(ba)
    BufferPool.release(bb)
    ba.toTypedArray.buffer
  }

  def awaitField(field: => js.UndefOr[String]): Task[String] = for {
    maybeString <- Task.eval(field).delayExecution(1.second)
    string <- maybeString.fold(awaitField(field))(Task.pure)
  } yield string

  private class PeerJSChannel(jsConn: js.Dynamic) extends Channel[A] {
    override def send(a: A): Task[Unit] = Task {
      jsConn.send(Pickle.intoBytes(a) |> toArrayBuffer)
    }

    override val received = PublishSubject[A]()

    jsConn.on("data", (data: ArrayBuffer) => {
      Unpickle[A].tryFromBytes(TypedArrayBuffer.wrap(data)) match {
        case Success(a) => received.onNext(a)
        case Failure(ex) => received.onError(ex)
      }
    })

    jsConn.on("error", (err: js.Dynamic) => {
      received.onError(js.JavaScriptException(err))
    })
  }

  override def makeOffer: Task[(Offer, Task[Channel[A]])] = for {
    peerJSInstance <- Task { jsnew(g.Peer)(literal(key = apiKey)) }
    id <- awaitField { peerJSInstance.id.asInstanceOf[js.UndefOr[String]] }
    offer = Offer(id)
    connectTask = Task.async[Channel[A]]{ (_, done) =>
      peerJSInstance.on("connection", completeOnConnection(done))
      Cancelable.empty
    }
  } yield (offer, connectTask)

  override def connect(offer: Offer): Task[Channel[A]] = Task.async[Channel[A]] { (_, done) =>
    val peerJSInstance = jsnew(g.Peer)(literal(key = apiKey))
    val conn = peerJSInstance.connect(offer.string)
    completeOnConnection(done)(conn)
    Cancelable.empty
  }

  private def completeOnConnection(done: Callback[Channel[A]]) = (conn: js.Dynamic) => {
    conn.on("open", () => done.onSuccess(new PeerJSChannel(conn)))
    conn.on("error", (err: js.Dynamic) => done.onError(js.JavaScriptException(err)))
  }
}
