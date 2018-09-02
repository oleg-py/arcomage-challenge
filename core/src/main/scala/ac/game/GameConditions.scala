package ac.game

import eu.timepit.refined.auto._
import player._

case class GameConditions (
  initialStats: Player,
  victoryConditions: VictoryConditions
) {
  def isVictory (s: CardScope): Boolean = {
    s.enemy.buildings.tower.value == 0 ||
      s.stats.buildings.tower.value >= victoryConditions.tower.value ||
      s.stats.resources.asSeq.forall(_.value >= victoryConditions.resources.value)
  }

  def initialState = CardScope(
    initialStats,
    initialStats,
    Vector()
  )
}

object GameConditions {
  def testing = GameConditions(
    Player(
      Buildings(25, 15),
      Resources.all(10),
      Resources.all(2)
    ),
    VictoryConditions(100, 200)
  )
}
