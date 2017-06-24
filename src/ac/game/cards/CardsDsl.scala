package ac.game
package cards

import monocle.Lens
import monocle.macros.GenLens
import player._

//noinspection TypeAnnotation
object CardsDsl {
  private type Op = State => State

  object play {
    val again = State.turnModifiers.modify(_ :+ PlayAgain)
  }

  object enemy {
    def magic : ComparisonBuilder = new ComparisonBuilder(GenLens[State](_.enemy.income.gems))
    def wall  : ComparisonBuilder = new ComparisonBuilder(GenLens[State](_.enemy.buildings.wall))
    def quarry: ComparisonBuilder = new ComparisonBuilder(GenLens[State](_.enemy.income.bricks))
  }

  object discard {
    val card = State.turnModifiers.modify(_ :+ ForceDiscard)
  }

  object swap {
    val walls: Op = s => {
      val playerToWall = GenLens[Player](_.buildings.wall)
      val playerWall   = playerToWall.get(s.stats)
      val enemyWall    = playerToWall.get(s.enemy)
      val swapWalls =
        (State.stats composeLens playerToWall set enemyWall) andThen
        (State.enemy composeLens playerToWall set playerWall)

      swapWalls(s)
    }
  }

  class ComparisonBuilder (val prop: Lens[State, Int]) {
    def is (value: Int): State => Boolean = s => prop.get(s) == value
    def < (enemyWord: enemy.type) = CompareToEnemys(prop.get, _ < _)
    def > (enemyWord: enemy.type) = CompareToEnemys(prop.get, _ > _)
    def > (value: Int): State => Boolean = s => prop.get(s) > value
    def := (enemyWord: enemy.type): AssignProperty = AssignProperty(prop)
    def := (word: ComparisonBuilder): State => State = s => prop.set(word.prop.get(s))(s)
  }

  case class AssignProperty(prop: Lens[State, Int], player: Lens[State, Player] = State.enemy) {
    def quarry: Op = s => prop.set(player.get(s).income.bricks)(s)
    def magic: Op = s => prop.set(player.get(s).income.gems)(s)
  }

  case class CompareToEnemys (
    left: State => Int,
    op: (Int, Int) => Boolean
  ) {
    private def compareTo(f: Player => Int) = (s: State) => op(left(s), f(s.enemy))

    def tower:   State => Boolean = compareTo(_.buildings.tower)
    def wall:    State => Boolean = compareTo(_.buildings.wall)
    def magic:   State => Boolean = compareTo(_.resources.gems)
    def quarry:  State => Boolean = compareTo(_.income.bricks)
    def dungeon: State => Boolean = compareTo(_.income.recruits)
  }

  case class StatWord(stat: Lens[Player, Int]) extends ComparisonBuilder(State.stats composeLens stat) {
    def apply(i: Int) = stat.modify(_ + i)
  }

  val brick, bricks     = StatWord(GenLens[Player](_.resources.bricks))
  val quarry, quarries  = StatWord(GenLens[Player](_.income.bricks))

  val gem, gems         = StatWord(GenLens[Player](_.resources.gems))
  val magic             = StatWord(GenLens[Player](_.income.gems))

  val recruit, recruits = StatWord(GenLens[Player](_.resources.recruits))
  val dungeon, dungeons = StatWord(GenLens[Player](_.income.recruits))

  val wall, walls       = StatWord(GenLens[Player](_.buildings.wall))
  val tower, towers     = StatWord(GenLens[Player](_.buildings.tower))

  case class TargetWord(lens: Lens[State, Player]) {
    def apply(dmg: Int) = lens composeLens GenLens[Player](_.buildings) modify (_ receiveDamage dmg)
  }

  val dealt    = TargetWord(State.enemy)
  val received = TargetWord(State.stats)

  implicit class NumberSyntax(i: Int) {
    def player(word: StatWord) = State.stats.modify(word(i))
    def enemy (word: StatWord) = State.enemy.modify(word(i))
    def all   (word: StatWord) = player(word) andThen enemy(word)

    def damage(word: TargetWord) = word(i)
  }

  private def combineActions(actions: Seq[Op]): Op = s => actions.foldLeft(s)((s, op) => op(s))

  class CardBuilder(name: String, worth: Int, discardable: Boolean) {

    private def buildCard(color: Card.Color, actions: Seq[Op]): Card = {
      val cost = color match {
        case Card.Red   => Resources(bricks = worth)
        case Card.Blue  => Resources(gems = worth)
        case Card.Green => Resources(recruits = worth)
      }
      Card(name, color, cost, combineActions(actions), discardable)
    }

    def brick(actions: Op*)  = buildCard(Card.Red, actions)
    def bricks(actions: Op*) = brick(actions: _*)

    def gem(actions: Op*) = buildCard(Card.Blue, actions)
    def gems(actions: Op*) = gem(actions: _*)

    def recruit(actions: Op*) = buildCard(Card.Green, actions)
    def recruits(actions: Op*) = recruit(actions: _*)
  }

  implicit class StringToCardBuilderOps(name: String) {
    def worth(worth: Int) = new CardBuilder(name, worth, discardable = true)
  }

  case class nondiscardable(name: String) {
    def worth(worth: Int) = new CardBuilder(name, worth, discardable = false)
  }

  case class Alternative (check: State => Boolean, onTrue: Op, onFalse: Op) extends Op {
    def apply(s: State): State = if (check(s)) onTrue(s) else onFalse(s)
    def otherwise (ops: Op*): Alternative = copy(onFalse = combineActions(ops))
  }

  def when (check: State => Boolean) (ops: Op*) = Alternative(check, combineActions(ops), identity)
}
