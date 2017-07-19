package ac.webapp
import ac.interactions.{State => SessionState}

case class RootState (
  current: SessionState = SessionState.NotInitialized,
  offer: String = ""
)

