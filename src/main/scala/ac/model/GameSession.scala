package ac.model

import ac.model.player.{Player, State}

case class GameSession (
  initialStats: Player,
  victoryConditions: GameConditions
) {
  def isVictory (s: State): Boolean = {
    s.enemy.buildings.tower <= 0 ||
      s.stats.buildings.tower >= victoryConditions.tower ||
      s.stats.resources.asSeq.forall(_ >= victoryConditions.resources)
  }

  def initialState = State(
    initialStats,
    initialStats,
    Vector()
  )
}

