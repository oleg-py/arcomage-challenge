package ac.frontend.states


sealed trait AppState

object AppState {
  case class User(name: String, email: Option[String])

  case object NameEntry extends AppState
  case object AwaitingHost extends AppState
  case class AwaitingGuest(connectionLink: String, me: User) extends AppState
  case class Playing(me: User, other: User) extends AppState
}