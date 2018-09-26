package ac.frontend.states


sealed trait AppState

object AppState {
  case class User(name: String, avatarUrl: String)

  case object NameEntry extends AppState
  case object AwaitingHost extends AppState // TODO remove this state
  case object SupplyingConditions extends AppState
  case class  AwaitingGuest(connectionLink: String) extends AppState
  case object Playing extends AppState
  case object Victory extends AppState
  case object Defeat extends AppState
}