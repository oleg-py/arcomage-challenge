package ac.communication

import ac.syntax._
import scala.concurrent.duration._
import scala.scalajs.js
import scala.scalajs.js.Dynamic.{literal, global, newInstance}
import scala.scalajs.js.typedarray._
import scala.util.{Failure, Success}

import boopickle.BufferPool
import boopickle.Default._
import monix.eval.{Callback, Task}
import monix.execution.Cancelable
import monix.reactive.Observable
import monix.reactive.subjects.PublishSubject

import java.nio.ByteBuffer

final class PeerJS[A: Pickler](apiKey: String) extends Protocol[Observable, Task, A] {
  override def makeOffer: Task[(Offer, Task[Channel])] = Task.defer {
    val peerJS = newPeerJS()

    val connectTask = Task.async[Channel]{ (_, done) =>
      peerJS.on("connection", completeOnConnection(done))
      Cancelable.empty
    }

    awaitField[String] { peerJS.id }
      .map(Offer)
      .map((_, connectTask))
  }

  override def connect(offer: Offer): Task[Channel] = Task.async[Channel] { (_, done) =>
    val peerJSInstance = newPeerJS()
    val conn = peerJSInstance.connect(offer.string)
    completeOnConnection(done)(conn)
    Cancelable.empty
  }

  private class PeerJSChannel (jsConn: js.Dynamic) extends Channel {
    override def send(a: A): Task[Unit] = Task {
      discard { jsConn.send(Pickle.intoBytes(a) |> toArrayBuffer) }
    }

    override val received = PublishSubject[A]()

    jsConn.on("data", (data: ArrayBuffer) => {
      Unpickle[A].tryFromBytes(TypedArrayBuffer.wrap(data)) match {
        case Success(a) => received.onNext(a)
        case Failure(ex) => received.onError(ex)
      }
    })

    jsConn.on("error", (err: Any) => {
      received.onError(js.JavaScriptException(err))
    })
  }

  private def completeOnConnection(done: Callback[Channel]) = (conn: js.Dynamic) => {
    conn.on("open", () => done.onSuccess(new PeerJSChannel(conn)))
    conn.on("error", (err: Any) => done.onError(js.JavaScriptException(err)))
  }

  private def toArrayBuffer(bb: ByteBuffer) = {
    val ba = Array.ofDim[Byte](bb.limit())
    bb.get(ba)
    BufferPool.release(bb)
    ba.toTypedArray.buffer
  }

  private def awaitField[R](field: => js.Dynamic): Task[R] = for {
    maybeString <- Task.eval(field)
      .map(_.asInstanceOf[js.UndefOr[R]])
      .delayExecution(125.millis)

    r <- maybeString.fold(awaitField[R](field))(Task.pure)
  } yield r

  private def newPeerJS() = newInstance(global.Peer)(literal(key = apiKey))
}
