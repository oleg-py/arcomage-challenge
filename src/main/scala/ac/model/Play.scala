package ac.model

import ac.model.player.{Player, State}
import ac.model.cards._

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

  val deck: Vector[Card] =
    (red cards) ++ (blue cards) ++ (green cards)

  val cardsByName: Map[String, Card] =
    deck.map(c => (c.name, c)).toMap
}
