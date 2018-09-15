package ac.frontend.states

import ac.frontend.states.AppState.User
import ac.game.flow.Notification


sealed trait GameMessage

case class OpponentReady(other: User) extends GameMessage
case class EngineNotification(n: Notification) extends GameMessage
