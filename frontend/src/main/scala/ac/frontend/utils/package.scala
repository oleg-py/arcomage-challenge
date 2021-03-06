package ac.frontend

import scala.scalajs.{LinkingInfo, js}

import cats.effect._
import fs2.Stream
import org.scalajs.dom.window
import scala.concurrent.duration._

import monix.execution.annotations.UnsafeBecauseImpure



package object utils {
  implicit class JSCastOps(private val self: js.Any) extends AnyVal {
    def jsCast[A]: A = self.asInstanceOf[A]
  }


  implicit final class StreamOps[F[_], A](private val self: Stream[F, A]) {
    def frameDebounced(implicit F: Concurrent[F], timer: Timer[F]): Stream[F, A] =
      self.debounce(16.millis) // ~ 1 frame, skips intermediate spinner
  }

  @UnsafeBecauseImpure
  def inDevelopment(): Boolean = LinkingInfo.developmentMode

  @UnsafeBecauseImpure
  def pageIsReloading(): Boolean =
    window.performance.navigation.`type` != 0

  implicit class ClassSetOps (sc: StringContext) {
    def cls(args: Any*): String = {
      val args2 = args.map {
        case s: String => s
        case (true, s: String) => s
        case (false, _: String) => ""
        case other =>
          throw new IllegalArgumentException(s"Unsupported className: $other")
      }
      sc.s(args2: _*)
    }
  }

  // TODO - delete; shouldn't link Peer to a global scheduler
  implicit class EffectOps[F[_], A](private val self: F[A]) extends AnyVal {
    @UnsafeBecauseImpure
    def unsafeRunLater()(implicit F: Effect[F]): Unit =
      F.runAsync(self) {
        case Right(_) => IO.unit
        case Left(ex) => IO(Main.scheduler.reportFailure(ex))
      }.unsafeRunSync()
  }
}
