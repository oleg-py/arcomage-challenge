package ac.interactions

import ac.game.Play._
import State._
import Event._
import ac.game.Randomizer
import cats.Applicative
import cats.syntax.option._
import cats.syntax.functor._

object Transitions {
  def apply[F[_]: Randomizer: Applicative]: OutcomeFn[F] = {
    case HostNameEntry                 -< NameEntered(name) =>
      WaitingForGuest(name).liftC(EnemyNameSet(name))

    case AwaitHostConnection           -< EnemyNameSet(name) =>
      GuestNameEntry(name).lift

    case GuestNameEntry(enemy)         -< NameEntered(name)  =>
      SelectConditions(name, enemy).liftC(EnemyNameSet(name))

    case SelectConditions(name, enemy) -< ConditionsChosen(conds) =>
      initialPlayerScope(name, enemy, conds)
        .map(EnemyTurn)
        .tupleRight(GuestReady(name, conds).some)
        .widen

    case (e @ WaitingForGuest(_))         -< EnemyNameSet(_) =>
      e.lift // TODO something should be done about this bit...

    case WaitingForGuest(name)         -< GuestReady(enemy, conds) =>
      initialPlayerScope(name, enemy, conds)
        .map(PlayerTurn)
        .tupleRight(none)
        .widen

    case PlayerTurn(p)                 -< PlayedCard(n) if p.canPlay(n) =>
      val (card, nextCards) = p.cards.pull(n)
      makeTurn(playCard(card), nextCards.some)(p).liftC(EnemyPlayedCard(card.name))

    case PlayerTurn(p)                 -< DiscardedCard(n) =>
      val (card, nextCards) = p.cards.pull(n)
      makeTurn(discardCard   , nextCards.some)(p).liftC(EnemyDiscardedCard(card.name))

    case EnemyTurn(p)                  -< EnemyPlayedCard(name) =>
      makeTurn(playEnemyCard(name))(p).lift

    case EnemyTurn(p)                  -< EnemyDiscardedCard(_) =>
      makeTurn(discardEnemyCard   )(p).lift
  }
}


