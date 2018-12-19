package ac.game

import ac.game.player.CardScope
import ac.game.cards._
import scala.language.postfixOps
import CardScope._
import mouse.all._

object Play {
  val cardByName: Map[String, Card] =
    Cards.allCards
      .map(c => (c.name, c))
      .toMap

  val playCard: Card => Card.Fn = card => _
    .thrush(turnMods.modify(_ drop 1))
    .thrush(stats.modify(_ addResources card.cost))
    .thrush(_.incomeOnTurn)
}
