package ac.game.cards.dsl

import ac.game.cards.dsl.ExecInterpreter._
import ac.game.cards.dsl.structure._
import ac.game.player.{CardScope, TurnMod}
import cats.data.Chain
import cats.kernel.Comparison.{EqualTo, GreaterThan, LessThan}
import cats.{Comparison, Endo}
import monocle.macros.GenLens
import qq.droste.{Algebra, scheme}
import cats.syntax.foldable._
import cats.instances.list._

object DescribeInterpreter {
  def apply(dslEntry: DSLEntry): Chain[String] =
    scheme.cata(algebra).apply(dslEntry)

  def stringify(comparison: Comparison): String = comparison match {
    case GreaterThan => ">"
    case EqualTo => "="
    case LessThan => "<"
  }

  def stringify(r: Read): String = r match {
    case Val(value) => value.toString
    case Var(target, prop) =>
      val tgs = target match {
        case structure.Player => "player"
        case structure.Enemy => "enemy"
      }

      val ps = prop match {
        case Income(Bricks) => "quarry"
        case Income(Gems) => "magic"
        case Income(Recruit) => "dungeon"
        case Avail(Bricks) => "bricks"
        case Avail(Gems) => "gems"
        case Avail(Recruit) => "recruits"
        case Wall => "wall"
        case Tower => "tower"
      }

      tgs + " " + ps
  }

  def stringify(c: Condition): String =
    stringify(c.x) ++ " " ++ stringify(c.op) ++ " " ++ stringify(c.other)

  def algebra: Algebra[DSLEntryF, Chain[String]] =
    Algebra {
      case  PlayAgain => Chain("Play again")
      case  SwapWalls => Chain("Swap walls")
      case  DiscardCard => Chain("Receive a card", "Discard a card")
      case  Assign(from, to) =>
        Chain(s"${stringify(from)} becomes as ${stringify(to)}")
      case  Modify(from, to) =>
        val str = s"$to to ${stringify(from)}"
        if (str startsWith "-") Chain(str)
        else Chain("+" + str)
      case  Damage(Player, value) =>
        Chain(s"You receive ${stringify(value)} damage")
      case  Damage(Enemy, value) =>
        Chain(s"${stringify(value)} damage")
      case  Alt(c, ifTrue, ifFalse) =>
        val prefix = Chain(s"if ${stringify(c)}") ++ ifTrue
        ifFalse.fold(prefix)(prefix.append("otherwise") ++ _)
      case  Combination(as) =>
        as.combineAll
    }
}
