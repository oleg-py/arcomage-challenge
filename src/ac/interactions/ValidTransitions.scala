package ac.interactions

import ac.game.Play._
import cats.syntax.option._
import State._, Command._

object ValidTransitions {
  // TODO tagless?
  val table: PartialFunction[(State, Command), OutcomeL] = {
    case HostNameEntry                 -< NameEntered(name) =>
      WaitingForGuest(name).liftC(EnemyNameSet(name))

    case AwaitHostConnection           -< EnemyNameSet(name) =>
      GuestNameEntry(name).lift

    case GuestNameEntry(enemy)         -< NameEntered(name)  =>
      SelectConditions(name, enemy).liftC(EnemyNameSet(name))

    case SelectConditions(name, enemy) -< ConditionsChosen(conds) =>
      initialPlayerScope(name, enemy, conds).map(EnemyTurn).liftC(GuestReady(name, conds))

    case WaitingForGuest(name)         -< GuestReady(enemy, conds) =>
      initialPlayerScope(name, enemy, conds).map(PlayerTurn).lift

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


