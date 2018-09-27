package ac.frontend.i18n

import ac.game.cards.Card
import ac.game.cards.Card.Color
import ac.game.cards.dsl.DescribeInterpreter
import ac.game.cards.dsl.structure.DSLEntry
import cats.data.Chain


object English extends Lang {
  override def cardName(str: String): String = str
  override def cardDescription(dslEntry: DSLEntry): Chain[String] =
    DescribeInterpreter(dslEntry)

  override def resourceName(color: Card.Color): String = color match {
    case Color.Red => "Bricks"
    case Color.Blue => "Gems"
    case Color.Green => "Recruits"
  }
}
