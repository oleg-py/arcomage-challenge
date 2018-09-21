package ac.frontend.states


sealed trait AppState

object AppState {
  case class User(name: String, avatarUrl: String)

  case object NameEntry extends AppState
  case object AwaitingHost extends AppState
  case class SupplyingConditions(me: User, other: User) extends AppState
  case class AwaitingGuest(connectionLink: String, me: User) extends AppState
  case class Playing(me: User, other: User) extends AppState
}