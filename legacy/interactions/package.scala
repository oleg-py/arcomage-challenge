package ac

import ac.syntax._
import ac.game.{GameConditions, Randomizer}
import ac.game.cards.Cards
import ac.game.player.{CardScope, PlayerScope}
import ac.interactions.State._
import cats.{Applicative, Functor}
import cats.syntax.applicative._
import cats.syntax.functor._
import cats.syntax.option._

package object interactions {
  type Outcome[F[_]] = F[(State, Option[Event])]
  type OutcomeFn[F[_]] = PartialFunction[(State, Event), Outcome[F]]

  object -< {
    def unapply[A, B](t: (A, B)) = Some(t)
  }

  implicit class TableSugar1[F[_]: Applicative](a: State) {
    def lift             : Outcome[F] = (a, none[Event]).pure[F]
    def liftC(c: Event): Outcome[F] = (a, c.some).pure[F]
  }

  private def nextTurnState(isEnemy: Boolean)(s: PlayerScope) = {
    val (playAgain, passTurn) =
      if (!isEnemy) (PlayerTurn, EnemyTurn)
      else          (EnemyTurn, PlayerTurn)

    s.thru {
      if (s.isVictory) Victory
      else if (s.isDefeat) Defeat
      else if (s.game.passTurn) passTurn
      else playAgain
    }
  }

  def makeTurn(
    f: CardScope => CardScope,
    cards: Option[Cards] = None
  ) = {
    cards.fold(identity[PlayerScope] _)(PlayerScope.cards.set) andThen
      PlayerScope.game.modify(f) andThen
      nextTurnState(isEnemy = cards.isEmpty)
  }

  def initialPlayerScope[F[_]: Randomizer: Functor](
    myName: String,
    enemyName: String,
    conds: GameConditions
  ) = Cards.initial(6).map(cards =>
    PlayerScope(
      myName,
      enemyName,
      cards,
      conds.initialState,
      conds
    )
  )
}
