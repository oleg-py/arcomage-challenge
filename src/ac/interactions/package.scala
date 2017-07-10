package ac

import ac.syntax._
import ac.game.GameConditions
import ac.game.cards.Cards
import ac.game.player.{CardScope, PlayerScope}
import ac.interactions.State._
import monix.eval.Task

package object interactions {
  type OutcomeL = Task[(State, Option[Command])]

  object -< {
    def unapply[A, B](t: (A, B)) = Some(t)
  }

  implicit class TableSugar1(a: State) {
    def lift             : OutcomeL = Task.pure((a, None))
    def liftC(c: Command): OutcomeL = Task.pure((a, Some(c)))
  }

  implicit class TableSugar2(task: Task[State]) {
    def lift             : OutcomeL = task.flatMap(_.lift)
    def liftC(c: Command): OutcomeL = task.flatMap(_.liftC(c))
  }

  private def nextTurnState(isEnemy: Boolean)(s: PlayerScope) = {
    val (playAgain, passTurn) =
      if (!isEnemy) (PlayerTurn, EnemyTurn)
      else          (EnemyTurn, PlayerTurn)

    s |> {
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

  def initialPlayerScope(myName: String, enemyName: String, conds: GameConditions) = for {
    cards <- Cards.initial(6)
  } yield PlayerScope(
    myName,
    enemyName,
    cards,
    conds.initialState,
    conds
  )
}
