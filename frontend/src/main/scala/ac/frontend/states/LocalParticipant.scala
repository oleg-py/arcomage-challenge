package ac.frontend.states

import ac.game.cards.Card
import ac.game.{GameConditions, Resources}
import ac.game.flow.{Notification, Participant, TurnIntent}
import eu.timepit.refined.types.numeric.NonNegInt
import cats.implicits._

/*_*/
class LocalParticipant[F[_]](implicit store: StoreAlg[F]) extends Participant[F] {
  import store.implicits._
  def proposeConditions: F[GameConditions] =
    concurrent.raiseError(new Exception("Remote player is supposed to propose conditions"))

  def getTurn(hand: Vector[Card], rsc: Resources[NonNegInt]): F[TurnIntent] =
    store.canMove.set(true) *>
      store.myTurnIntents.await1 { case ti => ti } <*
      store.canMove.set(false)

  def notify(notification: Notification): F[Unit] =
    store.gameEvents.emit1(EngineNotification(notification))
}
