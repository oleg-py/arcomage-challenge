package ac.frontend.states

import cats.implicits._
import ac.game.cards.Card
import ac.game.{GameConditions, Resources}
import ac.game.flow.{Notification, Participant, TurnIntent}
import eu.timepit.refined.types.numeric.NonNegInt

/*_*/
class RemoteParticipant[F[_]](implicit store: StoreAlg[F]) extends Participant[F] {
  import store.implicits._

  def proposeConditions: F[GameConditions] = store.gameEvents.await1 {
    case ConditionsSet(conds) => conds
  }

  def getTurn(hand: Vector[Card], rsc: Resources[NonNegInt]): F[TurnIntent] =
    store.send(RemoteTurnRequest(hand, rsc)) >> store.gameEvents.await1 {
      case RemoteTurnIntent(ti) => ti
    }

  def notify(notification: Notification): F[Unit] =
    store.send(EngineNotification(notification))
}
