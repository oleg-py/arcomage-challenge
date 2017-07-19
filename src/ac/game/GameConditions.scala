package ac.game

import player._

case class GameConditions (
  initialStats: Player,
  victoryConditions: VictoryConditions
) {
  def isVictory (s: CardScope): Boolean = {
    s.enemy.buildings.tower <= 0 ||
      s.stats.buildings.tower >= victoryConditions.tower ||
      s.stats.resources.asSeq.forall(_ >= victoryConditions.resources)
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
