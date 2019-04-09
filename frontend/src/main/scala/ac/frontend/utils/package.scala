package ac.frontend

import scala.scalajs.{LinkingInfo, js}

import cats.data.Nested
import cats.effect._
import cats.implicits._
import fs2.Stream
import org.scalajs.dom.window
import scala.concurrent.duration._

import cats.sequence.Sequencer
import monix.execution.annotations.UnsafeBecauseImpure
import shapeless.{Generic, HList, ProductArgs}



package object utils {
  implicit class JSCastOps(private val self: js.Any) extends AnyVal {
    def jsCast[A]: A = self.asInstanceOf[A]
  }


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

  def combine[A] = new CombinerCurriedArgs[A]

  class CombinerCurriedArgs[A] extends ProductArgs {
    def fromProduct[F[_], L <: HList, B <: HList](l: L)(implicit
      seq: Sequencer.Aux[L, Stream[F, ?], B],
      gen: Generic.Aux[A, B]
    ): Stream[F, A] = seq(l).map(gen.from)
  }

  @UnsafeBecauseImpure
  def isDevelopment(): Boolean = LinkingInfo.developmentMode

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
          throw new IllegalArgumentException(s"Unsupported className: ${other}")
      }
      sc.s(args2: _*)
    }
  }

  implicit class EffectOps[F[_], A](private val self: F[A]) extends AnyVal {
    @UnsafeBecauseImpure
    def unsafeRunLater()(implicit F: Effect[F]): Unit =
      F.runAsync(self) {
        case Right(_) => IO.unit
        case Left(ex) => IO(Main.scheduler.reportFailure(ex))
      }.unsafeRunSync()
  }
}
