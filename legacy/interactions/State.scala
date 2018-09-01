package ac.interactions

import ac.game.player.{CardScope, PlayerScope}


sealed trait State extends Product with Serializable {
  def comparable: Option[CardScope] = None
}

object State {
  sealed trait StateP extends State {
    val p: PlayerScope
    override def comparable: Option[CardScope] = Some(p.game)
  }

  case object NotInitialized                                       extends State

  // Host-only states
  case object HostNameEntry                                        extends State
  case class  WaitingForGuest  (myName: String)                    extends State

  // Guest-only states
  case object AwaitHostConnection                                  extends State
  case class  GuestNameEntry   (enemyName: String)                 extends State
  case class  SelectConditions (myName: String, enemyName: String) extends State

  // Shared states
  case class  PlayerTurn       (p: PlayerScope)                    extends StateP
  case class  EnemyTurn        (p: PlayerScope)                    extends StateP
  case class  Victory          (p: PlayerScope)                    extends StateP
  case class  Defeat           (p: PlayerScope)                    extends StateP
}
