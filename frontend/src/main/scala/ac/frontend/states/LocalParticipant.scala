package ac.frontend.states

import ac.game.cards.Card
import ac.game.{GameConditions, Resources}
import ac.game.flow.{Notification, Participant, TurnIntent}
import cats.effect.Sync
import eu.timepit.refined.types.numeric.NonNegInt

/*_*/
class LocalParticipant[F[_]: Sync](implicit store: StoreAlg[F]) extends Participant[F] {
  def proposeConditions: F[GameConditions] = store.gameEvents.await1 {
    case ConditionsSet(conds) => conds
  }

  def getTurn(hand: Vector[Card], rsc: Resources[NonNegInt]): F[TurnIntent] =
    store.myTurnIntents.await1 { case ti => ti }

  def notify(notification: Notification): F[Unit] =
    store.gameEvents.emit1(EngineNotification(notification))
}
