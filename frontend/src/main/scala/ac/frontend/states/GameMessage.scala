package ac.frontend.states

import ac.game.flow.Notification


sealed trait GameMessage

case class EngineNotification(n: Notification) extends GameMessage
