package ac.game.flow

import ac.game.{GameConditions, Resources}
import ac.game.cards.Card
import cats.Monad
import eu.timepit.refined.types.numeric.NonNegInt


trait Participant[F[_]] {
  def proposeConditions: F[GameConditions]
  protected def getTurn(hand: Vector[Card], rsc: Resources[NonNegInt]): F[TurnIntent]
  final def getValidIntent(hand: Vector[Card], rsc: Resources[NonNegInt])(implicit F: Monad[F]): F[TurnIntent] =
    F.iterateUntil(getTurn(hand, rsc))(_.canPlay(hand, rsc))
  def notify(notification: Notification): F[Unit]
}