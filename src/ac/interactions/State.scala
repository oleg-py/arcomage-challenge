package ac.interactions

import ac.game.player.PlayerScope


sealed trait State

object State {
  // Host-only states
  case object HostNameEntry                                        extends State
  case class  WaitingForGuest  (myName: String)                    extends State

  // Guest-only states
  case object AwaitHostConnection                                  extends State
  case class  GuestNameEntry   (enemyName: String)                 extends State
  case class  SelectConditions (myName: String, enemyName: String) extends State

  // Shared states
  case class  PlayerTurn       (p: PlayerScope)                    extends State
  case class  EnemyTurn        (p: PlayerScope)                    extends State
  case class  Victory          (p: PlayerScope)                    extends State
  case class  Defeat           (p: PlayerScope)                    extends State
}
