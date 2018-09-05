package ac.console

import ac.game.{GameConditions, Resources}
import ac.game.cards.Card
import ac.game.flow._
import cats.Applicative
import cats.implicits._
import eu.timepit.refined.types.numeric.NonNegInt

class MrDerp[F[_]: Applicative] extends Participant[F] {
  def getTurn(hand: Vector[Card], rsc: Resources[NonNegInt]): F[TurnIntent] = {
    val is = hand.indices.toStream.map(NonNegInt.unsafeFrom)
    (is.map(Play) #::: is.map(Discard)).find(_.canPlay(hand, rsc))
      .getOrElse(sys.error("No valid moves"))
      .pure[F]
  }

  def notify(notification: Notification): F[Unit] = ().pure[F]

  def proposeConditions: F[GameConditions] = GameConditions.testing.pure[F]
}
