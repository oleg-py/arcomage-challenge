package ac.game

import cats.kernel.Semigroup
import cats.syntax.semigroup._
import eu.timepit.refined.types.numeric.{NonNegInt, PosInt}

case class Resources[N] (
  bricks:   N,
  gems:     N,
  recruits: N
)(implicit N: IntLike[N]) {
  import IntLike.toInt
  def asSeq: List[N] = List(bricks, gems, recruits)
  def map[M: IntLike](f: N => M): Resources[M] = Resources(f(bricks), f(gems), f(recruits))


  def asInt: Resources[Int] = map(toInt[N])
  def min_1: Resources[PosInt] = asInt.map(PosInt.from(_).getOrElse(PosInt(1)))
  def min_0: Resources[NonNegInt] = asInt.map(NonNegInt.from(_).getOrElse(NonNegInt(0)))

  def |+|(r: Resources[N]): Resources[N] = Resources(
    bricks   |+| r.bricks,
    gems     |+| r.gems,
    recruits |+| r.recruits
  )

  def +[M](r: Resources[M]): Resources[Int] = this.asInt |+| r.asInt

  def -[M](r: Resources[M]): Resources[Int] = this + (-r)
  def unary_-(): Resources[Int] = this * -1

  def * (i: Int): Resources[Int] = asInt.map(i * _)

  def all_<=[M: IntLike](r: Resources[M]): Boolean = this.asSeq zip r.asSeq forall {
    case (a, b) => toInt(a) <= toInt(b)
  }
}

object Resources {
  def all[A: IntLike](x: A) = Resources(x, x, x)
}

trait IntLike[A] extends Semigroup[A] {
  def toInt(a: A): Int
}

object IntLike {
  def toInt[A](a: A)(implicit A: IntLike[A]): Int = A.toInt(a)

  implicit val intInstance: IntLike[Int] = new IntLike[Int] {
    def toInt(a: Int): Int = a
    def combine(x: Int, y: Int): Int = x + y
  }

  implicit val positiveInstance: IntLike[PosInt] = new IntLike[PosInt] {
    def toInt(a: PosInt): Int = a.value
    def combine(x: PosInt, y: PosInt): PosInt = PosInt.unsafeFrom(x.value + y.value)
  }

  implicit val nonNegInstance: IntLike[NonNegInt] = new IntLike[NonNegInt] {
    def toInt(a: NonNegInt): Int = a.value
    def combine(x: NonNegInt, y: NonNegInt): NonNegInt = NonNegInt.unsafeFrom(x.value + y.value)
  }
}