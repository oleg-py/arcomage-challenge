package ac.messaging.protocol

import scala.concurrent.duration._
import monix.eval.Task
import monix.execution.Cancelable
import monix.execution.atomic.AtomicInt


final class Peer[Req, Res] (comm: BiCommunicator[Req, Res]) {
  def request (payload: Req): Task[Res] = {
    val id = counter.getAndIncrement()
    requestor.send(id -> payload)
    requestor.received
      .findL(_._1 == id)
      .flatMap {
        case Some((_, response)) => Task.now(response)
        case None => Task.never
      }
      .timeout(Timeout)
  }

  def receive (respond: Req => Task[Res]): Task[Unit] = {
    Task.create[Unit]((sc, cb) => {
      respondent.received
        .mapTask { case (id, req) => respond(req).map(id -> _) }
        .doOnComplete(() => cb.onSuccess(()))
        .doOnError(cb.onError)
        .foreach(respondent.send)(sc)
      Cancelable.empty
    })
  }

  private val counter = AtomicInt(0)
  private val Timeout = 5.seconds

  private[this] val requestor = Communicator (
    (x: Numbered[Req]) => comm.send(Left(x)),
    comm.received.collect { case Right(x) => x }
  )

  private[this] val respondent = Communicator (
    (x: Numbered[Res]) => comm.send(Right(x)),
    comm.received.collect { case Left(x) => x }
  )
}
