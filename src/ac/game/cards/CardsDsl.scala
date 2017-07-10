package ac.game
package cards

import monocle.Lens
import monocle.macros.GenLens
import monocle.macros.syntax.lens._
import player._
import TurnMod._

//noinspection TypeAnnotation
object CardsDsl {
  object play {
    val again = CardScope.turnMods.modify(_ :+ PlayAgain)
  }

  object enemy {
    def magic : Attribute = new Attribute(GenLens[CardScope](_.enemy.income.gems))
    def wall  : Attribute = new Attribute(GenLens[CardScope](_.enemy.buildings.wall))
    def quarry: Attribute = new Attribute(GenLens[CardScope](_.enemy.income.bricks))
  }

  object discard {
    val card = CardScope.turnMods.modify(_ :+ ForceDiscard)
  }

  object swap {
    val walls: Card.Fn = s => {
      val Array(playerWall, enemyWall) = Array(s.stats, s.enemy).map(_.buildings.wall)
      s
        .lens(_.stats.buildings.wall).set(enemyWall)
        .lens(_.enemy.buildings.wall).set(playerWall)
    }
  }

  class Attribute (val prop: Lens[CardScope, Int]) {
    def is (value: Int): CardScope => Boolean = s => prop.get(s) == value
    def < (enemyWord: enemy.type) = AttributeCompare(prop.get, _ < _)
    def > (enemyWord: enemy.type) = AttributeCompare(prop.get, _ > _)
    def > (value: Int): CardScope => Boolean = s => prop.get(s) > value
    def := (enemyWord: enemy.type): AttributeRead = AttributeRead(prop)
    def := (word: Attribute): CardScope => CardScope = s => prop.set(word.prop.get(s))(s)
  }

  case class AttributeRead (prop: Lens[CardScope, Int]) {
    def quarry: Card.Fn = s => prop.set(CardScope.enemy.get(s).income.bricks)(s)
    def magic : Card.Fn = s => prop.set(CardScope.enemy.get(s).income.gems)(s)
  }

  case class AttributeCompare (
    left: CardScope => Int,
    op: (Int, Int) => Boolean
  ) {
    private def compareTo(f: Player => Int) = (s: CardScope) => op(left(s), f(s.enemy))

    def tower:   CardScope => Boolean = compareTo(_.buildings.tower)
    def wall:    CardScope => Boolean = compareTo(_.buildings.wall)
    def magic:   CardScope => Boolean = compareTo(_.resources.gems)
    def quarry:  CardScope => Boolean = compareTo(_.income.bricks)
    def dungeon: CardScope => Boolean = compareTo(_.income.recruits)
  }

  case class PlayerAttribute(stat: Lens[Player, Int]) extends Attribute(CardScope.stats composeLens stat) {
    def apply(i: Int) = stat.modify(_ + i)
  }

  val brick, bricks     = PlayerAttribute(GenLens[Player](_.resources.bricks))
  val quarry, quarries  = PlayerAttribute(GenLens[Player](_.income.bricks))

  val gem, gems         = PlayerAttribute(GenLens[Player](_.resources.gems))
  val magic             = PlayerAttribute(GenLens[Player](_.income.gems))

  val recruit, recruits = PlayerAttribute(GenLens[Player](_.resources.recruits))
  val dungeon, dungeons = PlayerAttribute(GenLens[Player](_.income.recruits))

  val wall, walls       = PlayerAttribute(GenLens[Player](_.buildings.wall))
  val tower, towers     = PlayerAttribute(GenLens[Player](_.buildings.tower))

  case class Target(lens: Lens[CardScope, Player]) {
    def apply(dmg: Int) = lens.composeLens(Player.buildings).modify(_ damageBy dmg)
  }

  val dealt    = Target(CardScope.enemy)
  val received = Target(CardScope.stats)

  implicit class NumberSyntax(i: Int) {
    def player(word: PlayerAttribute) = CardScope.stats.modify(word(i))
    def enemy (word: PlayerAttribute) = CardScope.enemy.modify(word(i))
    def all   (word: PlayerAttribute) = player(word) andThen enemy(word)

    def damage(word: Target) = word(i)
  }

  private def combineActions(actions: Seq[Card.Fn]): Card.Fn = s => actions.foldLeft(s)((s, op) => op(s))

  class CardBuilder(name: String, worth: Int, discardable: Boolean) {

    private def buildCard(color: Card.Color, actions: Seq[Card.Fn]): Card = {
      Card(name, color, worth, combineActions(actions), discardable)
    }

    def brick(actions: Card.Fn*)  = buildCard(Card.Color.Red, actions)
    def bricks(actions: Card.Fn*) = brick(actions: _*)

    def gem(actions: Card.Fn*) = buildCard(Card.Color.Blue, actions)
    def gems(actions: Card.Fn*) = gem(actions: _*)

    def recruit(actions: Card.Fn*) = buildCard(Card.Color.Green, actions)
    def recruits(actions: Card.Fn*) = recruit(actions: _*)
  }

  implicit class StringToCardBuilderOps(name: String) {
    def worth(worth: Int) = new CardBuilder(name, worth, discardable = true)
  }

  case class nondiscardable(name: String) {
    def worth(worth: Int) = new CardBuilder(name, worth, discardable = false)
  }

  case class Alternative (check: CardScope => Boolean, onTrue: Card.Fn, onFalse: Card.Fn) extends Card.Fn {
    def apply(s: CardScope): CardScope = if (check(s)) onTrue(s) else onFalse(s)
    def otherwise (ops: Card.Fn*): Alternative = copy(onFalse = combineActions(ops))
  }

  def when (check: CardScope => Boolean) (ops: Card.Fn*) = Alternative(check, combineActions(ops), identity)
}
