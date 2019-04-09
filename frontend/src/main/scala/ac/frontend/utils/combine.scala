package ac.frontend.utils

import cats.data.Nested
import cats.effect.Concurrent
import cats.sequence.Sequencer
import fs2.Stream
import fs2.concurrent.Signal
import shapeless.ops.hlist.Mapper
import shapeless.{Generic, HList, Poly1, ProductArgs}
import cats.implicits._
import monix.eval.Task

object combine {
  def apply[A](implicit gen: Generic[A] { type Repr <: HList }): CombineCurried[A, gen.Repr] = new CombineCurried[A, gen.Repr](gen)

  object holdCall extends Poly1 {
    implicit def holdStream[F[_]: Concurrent, A] = at[Stream[F, A]](s => Nested(s.holdOption))
  }

  class CombineCurried[A, Repr <: HList](gen: Generic.Aux[A, Repr]) extends ProductArgs {
    def fromProduct[L <: HList, M <: HList, B <: HList](l: L)(implicit
      holdAll: Mapper.Aux[holdCall.type, L, M],
      seq: Sequencer.Aux[M, Nested[Stream[Task, ?], Signal[Task, ?], ?], B],
      seq2: Sequencer.Aux[B, Option, Repr]
    ): Stream[Task, A] = seq(holdAll(l)).value
      .flatMap(_.discrete)
      .mapFilter(seq2(_))
      .map(gen.from)
  }
}
