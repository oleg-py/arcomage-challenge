package ac.game.cards.dsl

import cats.{Applicative, Eval, Foldable, Traverse}
import cats.kernel.Comparison
import qq.droste.data.Fix
import cats.instances.list._
import cats.instances.option._
import cats.syntax.traverse._
import cats.syntax.apply._
import cats.syntax.functor._

object structure {
  type DSLEntry = Fix[DSLEntryF]

  sealed trait DSLEntryF[+A]
  object DSLEntryF {
    implicit val traverseInstance: Traverse[DSLEntryF] = new Traverse[DSLEntryF] {
      def traverse[G[_], A, B](fa: DSLEntryF[A])(f: A => G[B])(implicit ev: Applicative[G]): G[DSLEntryF[B]] =
        fa match {
          case Alt(c, ifTrue, ifFalse) => (f(ifTrue), ifFalse.traverse(f))
            .mapN(Alt(c, _, _))
          case Combination(as) => as.traverse(f).map(Combination(_))
          case v: DSLEntryF[Nothing @unchecked] => ev.pure(v) // TODO: skolemization doesn't work for some reason
        }

      def foldLeft[A, B](fa: DSLEntryF[A], b: B)(f: (B, A) => B): B =
        fa match {
          case Alt(_, ifTrue, ifFalse) => (List(ifTrue) ++ ifFalse).foldLeft(b)(f)
          case Combination(as) => as.foldLeft(b)(f)
          case _ => b
        }

      def foldRight[A, B](fa: DSLEntryF[A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] =
        fa match {
          case Alt(_, ifTrue, ifFalse) =>
            Foldable[List].foldRight(List(ifTrue) ++ ifFalse, lb)(f)
          case Combination(as) => Foldable[List].foldRight(as, lb)(f)
          case _ => lb
        }
    }
  }

  case object PlayAgain extends DSLEntryF[Nothing]
  case object SwapWalls extends DSLEntryF[Nothing]
  case object DiscardCard extends DSLEntryF[Nothing]
  case class Assign(from: Var, to: Read) extends DSLEntryF[Nothing]
  case class Modify(from: Var, to: Int) extends DSLEntryF[Nothing]
  case class Damage(target: Target, value: Read) extends DSLEntryF[Nothing]
  case class Alt[+A](c: Condition, ifTrue: A, ifFalse: Option[A]) extends DSLEntryF[A]
  case class Combination[+A](as: List[A]) extends DSLEntryF[A]

  sealed trait Target
  case object Player extends Target
  case object Enemy extends Target

  sealed trait Property
  case class Income(rt: ResourceType) extends Property
  case class Avail(rt: ResourceType) extends Property
  case object Wall extends Property
  case object Tower extends Property

  sealed trait ResourceType
  case object Bricks extends ResourceType
  case object Gems extends ResourceType
  case object Recruit extends ResourceType

  sealed trait Read
  case class Var(target: Target, prop: Property) extends Read
  case class Val(value: Int) extends Read

  case class Condition(x: Var, op: Comparison, other: Read)
}

