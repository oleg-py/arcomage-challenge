package ac.model.player

import ac.model.Resources
import shapeless._
import ops.hlist.Mapper

case class Player (
  buildings : Buildings,
  resources : Resources,
  income    : Resources
) {
  //noinspection TypeAnnotation
  private object f0 extends Poly1 {
    implicit val intCase = at[Int](_ min 0)
  }

  //noinspection TypeAnnotation
  private object f1 extends Poly1 {
    implicit val intCase = at[Int](_ min 1)
  }

  private def minF[A, Repr <: HList](a: A, f: Poly)(
    implicit gen: Generic.Aux[A, Repr],
    mapper: Mapper.Aux[f.type, Repr, Repr]
  ): A = gen.from(mapper(gen.to(a)))

  def normalized = Player(
    minF(buildings, f0),
    minF(resources, f0),
    minF(income, f1)
  )
}
