package ac.interactions

import ac.game.{GameConditions => Conds}


sealed trait Command

object Command {
  case class NameEntered        (name: String)                    extends Command
  case class EnemyNameSet       (name: String)                    extends Command
  case class GuestReady         (enemyName: String, conds: Conds) extends Command
  case class ConditionsChosen   (conds: Conds)                    extends Command
  case class PlayedCard         (n: Int)                          extends Command
  case class DiscardedCard      (n: Int)                          extends Command
  case class EnemyPlayedCard    (cardName: String)                extends Command
  case class EnemyDiscardedCard (cardName: String)                extends Command
}
