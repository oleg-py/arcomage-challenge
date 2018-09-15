package ac.frontend.states


sealed trait AppState

object AppState {
  case class User(name: String, email: Option[String])

  case object NameEntry extends AppState
  case class AwaitingGuest(id: String, me: User) extends AppState
//  case class Playing(me: User, other: User, game: GameState) extends AppState
}