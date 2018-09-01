package ac.interactions

import ac.game.{GameConditions => Conds}


sealed trait Event extends Product with Serializable

object Event {
  case class  NameEntered        (name: String)                    extends Event
  case class  EnemyNameSet       (name: String)                    extends Event
  case class  GuestReady         (enemyName: String, conds: Conds) extends Event
  case class  ConditionsChosen   (conds: Conds)                    extends Event
  case class  PlayedCard         (n: Int)                          extends Event
  case class  DiscardedCard      (n: Int)                          extends Event
  case class  EnemyPlayedCard    (cardName: String)                extends Event
  case class  EnemyDiscardedCard (cardName: String)                extends Event
}
