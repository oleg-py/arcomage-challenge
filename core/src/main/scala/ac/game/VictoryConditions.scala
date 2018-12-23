package ac.game

import ac.game.flow.EndStatus
import ac.game.flow.Notification.{Defeat, Draw, Victory}
import ac.game.player.CardScope
import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.auto._
import shapeless.Coproduct


case class VictoryConditions (tower: PosInt, resources: PosInt) {
  def isVictory (s: CardScope): Boolean = {
    s.enemy.buildings.tower.value == 0 ||
      s.stats.buildings.tower.value >= tower.value ||
      s.stats.resources.asSeq.forall(_.value >= resources.value)
  }

  def status (s: CardScope): Option[EndStatus] =
    (isVictory(s), isVictory(s.reverse)) match {
      case (false, false) => None
      case (true , true ) => Some(EndStatus(Coproduct[EndStatus.C](Draw)))
      case (true , _    ) => Some(EndStatus(Coproduct[EndStatus.C](Victory)))
      case (_,     true ) => Some(EndStatus(Coproduct[EndStatus.C](Defeat)))
    }
}

object VictoryConditions {
  val Dummy = VictoryConditions(1, 1)
}
