package ac.game

import ac.game.player.CardScope
import eu.timepit.refined.types.numeric.PosInt


case class VictoryConditions (tower: PosInt, resources: PosInt) {
  def isVictory (s: CardScope): Boolean = {
    s.enemy.buildings.tower.value == 0 ||
      s.stats.buildings.tower.value >= tower.value ||
      s.stats.resources.asSeq.forall(_.value >= resources.value)
  }
}
