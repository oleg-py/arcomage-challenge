package ac.game

import ac.game.player.CardScope
import ac.game.cards._
import scala.language.postfixOps
import ac.syntax._
import CardScope._

object Play {
  val cardByName: Map[String, Card] =
    Vector(red cards, blue cards, green cards)
      .flatten
      .map(c => (c.name, c))
      .toMap

  val playCard: Card => Card.Fn = card =>
    turnMods.modify(_ drop 1) andThen
    stats.modify(_ addResources card.cost) andThen
      card                                          andThen
      modifyIf(_.passTurn, enemy.modify(_.addResources()))

  val playEnemyCard: String => Card.Fn = str => cs =>
    cardByName(str) |> playCard |> cs.applyReversed

  val discardCard      : Card.Fn = playCard(Card.Noop)
  val discardEnemyCard : Card.Fn = _.applyReversed(discardCard)
}
