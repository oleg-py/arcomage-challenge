package ac.game.cards.dsl

import ac.game.Resources
import ac.game.cards.dsl.structure._
import ac.game.player.{Buildings, CardScope, TurnMod, Player => Stats}
import cats.arrow.Compose
import cats.{Endo, MonoidK}
import eu.timepit.refined.auto._
import eu.timepit.refined.types.numeric.{NonNegInt, PosInt}
import qq.droste._
import monocle.Lens
import cats.syntax.foldable._
import cats.instances.function._
import cats.instances.list._
import cats.instances.int._
import cats.syntax.order._
import cats.syntax.compose._
import monocle.macros.GenLens

object ExecInterpreter {
  def apply(dslEntry: DSLEntry): Endo[CardScope] =
    scheme.cata(algebra).apply(dslEntry)

  def algebra: Algebra[DSLEntryF, Endo[CardScope]] =
    Algebra {
      case  PlayAgain => CardScope.turnMods.modify(TurnMod.PlayAgain +: _)
      case  SwapWalls => cs =>
        val pw = cs.stats.buildings.wall
        val ew = cs.enemy.buildings.wall
        val fn = GenLens[CardScope](_.stats.buildings.wall).set(ew) >>>
          GenLens[CardScope](_.enemy.buildings.wall).set(pw)
        fn(cs)
      case  DiscardCard => CardScope.turnMods.modify(TurnMod.ForceDiscard +: _)
      case  Assign(from, to) =>
        cs => asLens(from).set(read(to)(cs))(cs)
      case  Modify(from, to) =>
        asLens(from).modify(_ + to)
      case  Damage(Player, value) =>
        damage(CardScope.stats, value)
      case  Damage(Enemy, value) =>
        damage(CardScope.enemy, value)
      case  Alt(c, ifTrue, ifFalse) =>
        cs => if (evalCondition(c, cs)) ifTrue(cs) else ifFalse.fold(cs)(_(cs))
      case  Combination(as) =>
        as.foldK
    }

  def asLens(p: Property): Lens[Stats, Int] = p match {
    case Income(Bricks) => GenLens[Stats](_.income.bricks) ^|-> pos
    case Income(Gems) => GenLens[Stats](_.income.gems) ^|-> pos
    case Income(Recruit) => GenLens[Stats](_.income.recruits) ^|-> pos
    case Avail(Bricks) => GenLens[Stats](_.resources.bricks) ^|-> nonNeg
    case Avail(Gems) => GenLens[Stats](_.resources.gems) ^|-> nonNeg
    case Avail(Recruit) => GenLens[Stats](_.resources.recruits) ^|-> nonNeg
    case structure.Wall =>
      Stats.buildings ^|-> Buildings.wall ^|-> nonNeg
    case structure.Tower =>
      Stats.buildings ^|-> Buildings.tower ^|-> nonNeg
  }
  def asLens(v: Var): Lens[CardScope, Int] = v.target match {
    case Player => CardScope.stats ^|-> asLens(v.prop)
    case Enemy => CardScope.enemy ^|-> asLens(v.prop)
  }

  def read(r: Read): CardScope => Int = r match {
    case v @ Var(_, _) => asLens(v).get _
    case Val(value) => _ => value
  }

  def damage(stats: Lens[CardScope, Stats], value: Read): Endo[CardScope] = cs => {
    NonNegInt.from(read(value)(cs))
      .map { amount =>
        (stats composeLens Stats.buildings).modify(_.damageBy(amount))(cs)
      }
      .getOrElse(cs)
  }

  def evalCondition(c: Condition, cs: CardScope): Boolean = {
    val Condition(lhs, expected, rhs) = c
    read(lhs)(cs).comparison(read(rhs)(cs)) == expected
  }

  private val nonNeg = Lens[NonNegInt, Int](_.value)(int => _ => NonNegInt.from(int).getOrElse(0))
  private val pos = Lens[PosInt, Int](_.value)(int => _ => PosInt.from(int).getOrElse(1))

}
