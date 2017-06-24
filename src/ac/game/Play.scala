package ac.game

import ac.game.player.{Player, State}
import ac.game.cards._
import cats.syntax.all._
import cats.instances.function._
import scala.language.postfixOps

object Play {
  val normalize: State => State =
    State.stats.modify(_.normalized) andThen
    State.enemy.modify(_.normalized)

  val reverse: State => State = _.reverse

  val playMyCard: (State => State) => State => State =
    _ andThen normalize

  val playEnemyCard: (State => State) => State => State =
    reverse andThen playMyCard(_) andThen reverse

  val playerIncome: State => State =
    State.stats.modify(p =>
      Player.resources.modify(_ + p.income)(p)
    )

  val enemyIncome: State => State =
    reverse andThen playerIncome andThen reverse

  val playerIncomeOnTurn: State => State =
    s => if (s.turnModifiers.isEmpty) playerIncome(s) else s

  val deck: Vector[Card] =
    (red cards) ++ (blue cards) ++ (green cards)

  val cardsByName: Map[String, Card] =
    deck.map(c => (c.name, c)).toMap

  val playSequence: String => State => State =
    (playEnemyCard contramap cardsByName)(_) map playerIncomeOnTurn
}
