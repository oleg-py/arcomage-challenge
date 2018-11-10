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
  val en = scheme.cata(En.algebra)
  val ru = scheme.cata(Ru.algebra)

  private object En {
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

  private object Ru {
    def stringify(c: Condition): String =
      stringify(c.x) ++ " " ++ En.stringify(c.op) ++ " " ++ stringify(c.other)

    def stringify(c: Read, genitive: Boolean = false): String = c match {
      case Val(value) => value.toString
      case Var(target, prop) =>
        def tgs(x: String) = target match {
          case Player if genitive && x.endsWith("м") => s"вашим $x"
          case Player if genitive => s"вашей $x"
          case Enemy => s"$x врага"
          case _ => x
        }

        val ps = prop match {
          case Income(Bricks) => if (genitive) "рудникам" else "рудники"
          case Income(Gems) => if (genitive) "магии" else "магия"
          case Income(Recruit) => if (genitive) "темницам" else "темницы"
          case Avail(Bricks) => if (genitive) "кирпичам" else "кирпичи"
          case Avail(Gems) => if (genitive) "драгоценностям" else "драгоценности"
          case Avail(Recruit) => if (genitive) "рекрутам" else "рекруты"
          case Wall  => if (genitive) "стене" else "стена"
          case Tower => if (genitive) "башне" else "башня"
        }

        tgs(ps)
    }

    def algebra: Algebra[DSLEntryF, Chain[String]] =
      Algebra {
        case PlayAgain => Chain("Ходите снова.")
        case SwapWalls => Chain("Ваши стены меняются местами")
        case DiscardCard => Chain("Получите карту", "Сбросьте карту")
        case Assign(from, to) =>
          Chain(s"${stringify(from)} становится равна ${stringify(to, genitive = true)}")
        case Modify(from, to) =>
          val str = s"$to к ${stringify(from, genitive = true)}"
          if (str startsWith "-") Chain(str)
          else Chain("+" + str)
        case Damage(Player, value) =>
          Chain(s"Вы получаете ${stringify(value)} ед. урона")
        case Damage(Enemy, value) =>
          Chain(s"${stringify(value)} ед. урона")
        case Alt(c, ifTrue, ifFalse) =>
          val prefix = Chain(s"Если ${stringify(c)}") ++ ifTrue
          ifFalse.fold(prefix)(prefix.append("Иначе") ++ _)
        case Combination(as) =>
          as.combineAll
      }
  }
}
