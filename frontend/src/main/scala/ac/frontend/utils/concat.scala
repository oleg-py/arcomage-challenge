package ac.frontend.utils

import shapeless.DepFn2
import shapeless.ops.tuple.Prepend


private[utils] trait Concat[A, B] extends DepFn2[A, B]

private[utils] object Concat extends ConcatLowPriority {
  type Aux[A, B, Out0] = Concat[A, B] { type Out = Out0 }
  implicit def tuples[A <: { def _1: Any }, B](implicit p: Prepend[A, Tuple1[B]]): Aux[A, B, p.Out] =
    new Concat[A, B] {
      type Out = p.Out
      def apply(t: A, u: B): Out = p(t, Tuple1(u))
    }
}

private[utils] trait ConcatLowPriority {
  implicit def base[A, B]: Concat.Aux[A, B, (A, B)] = new Concat[A, B] {
    type Out = (A, B)
    def apply(t: A, u: B): Out = (t, u)
  }
}
