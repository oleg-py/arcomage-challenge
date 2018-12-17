package ac.frontend

import scala.scalajs.LinkingInfo

import cats.data.Nested
import cats.effect.Concurrent
import fs2.Stream
import cats.implicits._
import shapeless.DepFn2
import shapeless.ops.tuple._


package object utils {
  trait Concat[A, B] extends DepFn2[A, B]

  object Concat extends ConcatLowPriority {
    type Aux[A, B, Out0] = Concat[A, B] { type Out = Out0 }
    implicit def tuples[A <: { def _1: Any }, B](implicit p: Prepend[A, Tuple1[B]]): Aux[A, B, p.Out] =
      new Concat[A, B] {
        type Out = p.Out
        def apply(t: A, u: B): Out = p(t, Tuple1(u))
      }
  }

  trait ConcatLowPriority {
    implicit def base[A, B]: Concat.Aux[A, B, (A, B)] = new Concat[A, B] {
      type Out = (A, B)
      def apply(t: A, u: B): Out = (t, u)
    }
  }

  implicit class StreamOps[F[_], A](private val self: Stream[F, A]) {
    def withLatestFrom[B](other: Stream[F, B])(implicit c: Concat[A, B], F: Concurrent[F]): Stream[F, c.Out] =
      Nested(self.holdOption).product(Nested(other.holdOption)).value
        .flatMap(_.discrete)
        .collect {
          case (Some(a), Some(b)) => c(a, b)
        }
  }

  def isDevelopment: Boolean = LinkingInfo.developmentMode

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
}
