package ac.frontend.actions

import ac.frontend.states.StoreAlg
import ac.game.flow.{Discard, Play}
import eu.timepit.refined.types.numeric.NonNegInt


object card {
  def play[F[_]](idx: NonNegInt)(implicit Store: StoreAlg[F]): F[Unit] =
    Store.myTurnIntents.emit1(Play(idx))

  def discard[F[_]](idx: NonNegInt)(implicit Store: StoreAlg[F]): F[Unit] =
    Store.myTurnIntents.emit1(Discard(idx))
}
