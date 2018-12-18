package ac.frontend

import scala.scalajs.LinkingInfo

import cats.data.Nested
import cats.effect.{Bracket, Concurrent, Timer}
import cats.implicits._
import cats.effect.implicits._
import fs2.Stream
import org.scalajs.dom.window
import scala.concurrent.duration._

import cats.effect.concurrent.Ref


package object utils {
  implicit final class StreamOps[F[_], A](private val self: Stream[F, A]) {
    def withLatestFrom[B](other: Stream[F, B])(implicit c: Concat[A, B], F: Concurrent[F]): Stream[F, c.Out] =
      Nested(self.holdOption).product(Nested(other.holdOption)).value
        .flatMap(_.discrete)
        .collect {
          case (Some(a), Some(b)) => c(a, b)
        }

    def frameDebounced(implicit F: Concurrent[F], timer: Timer[F]): Stream[F, A] =
      self.debounce(16.millis) // ~ 1 frame, skips intermediate spinner
  }

  def isDevelopment: Boolean = LinkingInfo.developmentMode

  def pageIsReloading(): Boolean =
    window.performance.navigation.`type` != 0

  implicit class ClassSetOps (sc: StringContext) {
    def cls(args: Any*): String = {
      val args2 = args.map {
        case s: String => s
        case (true, s: String) => s
        case (false, _: String) => ""
        case other =>
          throw new IllegalArgumentException(s"Unsupported className: ${other}")
      }
      sc.s(args2: _*)
    }
  }

  implicit class RefOps[F[_], A] (private val self: Ref[F, A]) extends AnyVal {
    def bind[B, E](a: A)(f: F[B])(implicit F: Bracket[F, E]): F[B] =
      self.getAndSet(a).bracket(_ => f)(self.set)
  }
}
