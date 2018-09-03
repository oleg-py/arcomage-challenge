package ac.game.cards.dsl

import scala.language.{implicitConversions, postfixOps}

import ac.game.cards.Card
import cats.kernel.Comparison
import eu.timepit.refined.types.numeric.NonNegInt
import qq.droste.data.Fix
import structure._

object lang {
  implicit private def lift(t: DSLEntryF[Fix[DSLEntryF]]): DSLEntry = Fix(t)

  object play {
    val again: DSLEntry = PlayAgain
  }
  object swap {
    val walls: DSLEntry = SwapWalls
  }

  object discard {
    val card: DSLEntry = DiscardCard
  }

  object enemy {
    val magic : Var = Var(Enemy, Income(Gems))
    val wall  : Var = Var(Enemy, Wall)
    val quarry: Var = Var(Enemy, Income(Bricks))
  }

  class Attribute private[lang] (val prop: Property)
  object Attribute {
    implicit def ops(attribute: Attribute): CompareBuilder =
      new CompareBuilder(Var(Player, attribute.prop))
  }


  val brick, bricks = new Attribute(Avail(Bricks))
  val quarry, quarries = new Attribute(Income(Bricks))

  val gem, gems = new Attribute(Avail(Gems))
  val magic = new Attribute(Income(Gems))

  val recruit, recruits = new Attribute(Avail(Recruit))
  val dungeon, dungeons = new Attribute(Income(Recruit))

  val wall, walls = new Attribute(Wall)
  val tower, towers = new Attribute(Tower)

  val dealt: Target = Enemy
  val received: Target = Player

  implicit class CompareBuilder (private val self: Var) extends AnyVal {
    def is(value: Int): Condition = Condition(self, Comparison.EqualTo, Val(value))
    def < (enemyWord: enemy.type) = TargetCompareBuilder(self, Comparison.LessThan, Enemy)
    def > (enemyWord: enemy.type) = TargetCompareBuilder(self, Comparison.GreaterThan, Enemy)
    def > (value: Int): Condition = Condition(self, Comparison.GreaterThan, Val(value))

    def := (enemyWord: enemy.type): EnemySet = EnemySet(self)
    def := (word: Attribute): DSLEntry = Assign(self, Var(Player, word.prop))
    def := (word: Var): DSLEntry = Assign(self, word)
  }

  case class TargetCompareBuilder(self: Var, cmp: Comparison, target: Target) {
    def tower:   Condition = Condition(self, cmp, Var(target, Tower))
    def wall:    Condition = Condition(self, cmp, Var(target, Wall))
    def magic:   Condition = Condition(self, cmp, Var(target, Income(Gems)))
    def quarry:  Condition = Condition(self, cmp, Var(target, Income(Bricks)))
    def dungeon: Condition = Condition(self, cmp, Var(target, Income(Recruit)))
  }

  case class EnemySet(self: Var) {
    def quarry: DSLEntry = Assign(self, enemy quarry)
    def magic : DSLEntry = Assign(self, enemy magic)
  }

  implicit class NumberSyntax (private val i: Int) extends AnyVal {
    def player (x: Attribute): DSLEntry = Modify(Var(Player, x.prop), i)
    def enemy  (x: Attribute): DSLEntry = Modify(Var(Player, x.prop), i)
    def all    (x: Attribute): DSLEntry = Combination(List(player(x), enemy(x)))

    def damage (word: Target): DSLEntry = Damage(word, Val(i))
  }

  def when (condition: Condition)(ops: DSLEntry*): PartialCondition =
    new PartialCondition(condition, ops)

  class PartialCondition(condition: Condition, ops: Seq[DSLEntry]) {
    def otherwise(ops2: DSLEntry*): DSLEntry =
      Alt(
        condition,
        Combination(ops.toList): DSLEntry,
        Some(Combination(ops2.toList): DSLEntry)
      )

    private def build: DSLEntry = Alt(condition, Combination(ops.toList): DSLEntry, None)
  }

  object PartialCondition {
    implicit def asDSLEntry(pc: PartialCondition): DSLEntry = pc.build
  }

    class CardBuilder(name: String, worth: NonNegInt, discardable: Boolean) {

      private def buildCard(color: Card.Color, actions: Seq[DSLEntry]): Card = {
        Card(name, color, worth, Combination(actions.toList), discardable)
      }

      def brick(actions: DSLEntry*): Card = buildCard(Card.Color.Red, actions)
      def bricks(actions: DSLEntry*): Card = brick(actions: _*)

      def gem(actions: DSLEntry*): Card = buildCard(Card.Color.Blue, actions)
      def gems(actions: DSLEntry*): Card = gem(actions: _*)

      def recruit(actions: DSLEntry*): Card = buildCard(Card.Color.Green, actions)
      def recruits(actions: DSLEntry*): Card = recruit(actions: _*)
    }

    implicit class StringToCardBuilderOps(name: String) {
      def worth(worth: NonNegInt) = new CardBuilder(name, worth, discardable = true)
    }

    case class nondiscardable(name: String) {
      def worth(worth: NonNegInt) = new CardBuilder(name, worth, discardable = false)
    }

}
