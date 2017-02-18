package ac.messaging

import ac.model.player.State

sealed trait GameEvent

case class NameChanged  (name: String) extends GameEvent
case class CardPlayed   (card: String) extends GameEvent
case class StateUpdated (state: State) extends GameEvent
