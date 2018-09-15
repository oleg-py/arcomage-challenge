package shironeko

import cats.effect.ConcurrentEffect
import cats.effect.implicits._
import fs2.Stream
import fs2.concurrent.{SignallingRef, Topic}
import cats.syntax.functor._


class Shironeko[F[_]](
  F: => ConcurrentEffect[F]
) {
  protected implicit def concurrentEffect: ConcurrentEffect[F] = F

  private def unsafeSignallingRef[A](initial: A): SignallingRef[F, A] = {
    SignallingRef[F, A](initial).toIO.unsafeRunSync()
  }

  type Cell[A] = SignallingRef[F, A]
  object Cell {
    def apply[A](a: A): Cell[A] = unsafeSignallingRef(a)
  }

  def preload[A](fa: F[A]): F[A] =
    fa.start.toIO.unsafeRunSync().join

  class Events[A] private (topic: Topic[F, Option[A]]) {
    def notify(as: Stream[F, A]): F[Unit] =
      as.map(Some(_)).to(topic.publish).compile.drain.start.void
    def notify1(a: A): F[Unit] = topic.publish1(Some(a))
  }

  object Events {
    def apply[A](f: A => F[Unit]): Events[A] = {
      val topic = Topic[F, Option[A]](None).toIO.unsafeRunSync()
      val bus = new Events[A](topic)
      topic.subscribe(1).unNone.evalMap(f)
        .compile.drain.toIO.unsafeRunAsyncAndForget()
      bus
    }
  }

  def dispatch(action: F[Unit]): Unit = {
    action.toIO.unsafeRunAsyncAndForget()
  }

}
