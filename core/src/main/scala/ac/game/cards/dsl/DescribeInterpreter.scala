package ac.game.cards.dsl

import ac.game.cards.dsl.ExecInterpreter._
import ac.game.cards.dsl.structure._
import ac.game.player.{CardScope, TurnMod}
import cats.kernel.Comparison.{EqualTo, GreaterThan, LessThan}
import cats.{Comparison, Endo}
import monocle.macros.GenLens
import qq.droste.{Algebra, scheme}


object DescribeInterpreter {
  def apply(dslEntry: DSLEntry): String =
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
    stringify(c.x) ++ stringify(c.op) ++ stringify(c.other)

  def algebra: Algebra[DSLEntryF, String] =
    Algebra {
      case  PlayAgain => "Play again"
      case  SwapWalls => "Swap walls"
      case  DiscardCard => "Receive a card\nDiscard a card"
      case  Assign(from, to) =>
        s"${stringify(from)} becomes as ${stringify(to)}"
      case  Modify(from, to) =>
        val str = s"$to to ${stringify(from)}"
        if (str startsWith "-") str
        else "+" + str
      case  Damage(Player, value) =>
        s"You receive ${stringify(value)} damage"
      case  Damage(Enemy, value) =>
        s"${stringify(value)} damage"
      case  Alt(c, ifTrue, ifFalse) =>
        s"if ${stringify(c)}\n$ifTrue\notherwise\n$ifFalse"
      case  Combination(as) =>
        as.mkString("\n")
    }
}
