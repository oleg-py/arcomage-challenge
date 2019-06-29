package ac.frontend.i18n

import ac.game.cards.Card
import ac.game.cards.Card.Color
import ac.game.cards.dsl.DescribeInterpreter
import ac.game.cards.dsl.structure.DSLEntry
import cats.data.Chain


sealed trait Lang {
  def cardName(str: String): String = str
  def cardDescription(dslEntry: DSLEntry): Chain[String] =
    DescribeInterpreter.en(dslEntry)

  def resourceName(color: Card.Color): String = color match {
    case Color.Red   => "Bricks"
    case Color.Blue  => "Gems"
    case Color.Green => "Recruits"
  }
}

object Lang {
  object En extends Lang
  object Ru extends Lang {
    override def cardDescription(dslEntry: DSLEntry): Chain[String] =
      DescribeInterpreter.ru(dslEntry)

    override def resourceName(color: Color): String = color match {
      case Color.Red   => "Кирпичи"
      case Color.Blue  => "Драгоценности"
      case Color.Green => "Звери"
    }
  }
}