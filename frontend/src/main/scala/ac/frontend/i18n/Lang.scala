package ac.frontend.i18n

import ac.game.cards.Card
import ac.game.cards.dsl.structure.DSLEntry
import cats.data.Chain


trait Lang {
  def cardName(str: String): String
  def cardDescription(dslEntry: DSLEntry): Chain[String]
  def resourceName(color: Card.Color): String
}
