package ac.game.cards.dsl

import cats.Traverse
import cats.kernel.Comparison
import qq.droste.data.Fix
import cats.derived.semi

object structure {
  type DSLEntry = Fix[DSLEntryF]

  sealed trait DSLEntryF[+A]
  object DSLEntryF {
    implicit val traverseInstance: Traverse[DSLEntryF] =
      semi.traverse[DSLEntryF]
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

