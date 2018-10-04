package ac.frontend.actions

import ac.frontend.states.StoreAlg
import ac.game.flow.TurnIntent
import eu.timepit.refined.types.numeric.NonNegInt


object card {
  def play[F[_]](idx: NonNegInt)(implicit Store: StoreAlg[F]): F[Unit] =
    Store.myTurnIntents.emit1(TurnIntent.Play(idx))

  def discard[F[_]](idx: NonNegInt)(implicit Store: StoreAlg[F]): F[Unit] =
    Store.myTurnIntents.emit1(TurnIntent.Discard(idx))
}
