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

