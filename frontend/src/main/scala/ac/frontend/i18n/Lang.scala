package ac.frontend.i18n

import ac.game.cards.Card
import ac.game.cards.Card.Color
import ac.game.cards.dsl.DescribeInterpreter
import ac.game.cards.dsl.structure.DSLEntry
import cats.data.Chain


trait Lang {
  def cardName(str: String): String = str
  def cardDescription(dslEntry: DSLEntry): Chain[String] =
    DescribeInterpreter.en(dslEntry)

  def resourceName(color: Card.Color): String = color match {
    case Color.Red   => "Bricks"
    case Color.Blue  => "Gems"
    case Color.Green => "Recruits"
  }

  def sNickname  = "Nickname"
  def sEmail     = "Email (optional - for avatar only)"
  def sEnterGame = "Enter a game"

  def sShareLink = "Share this link with your friend to start a game"

  def sCards      = "Cards"
  def sTowerToWin = "Tower to win"
  def sResToWin   = "Resources to win"
  def sTower      = "Tower"
  def sWall       = "Wall"
  def sIncome     = "Income"
  def sResources  = "Resources"
  def sConfirm    = "Confirm"
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

    override def sNickname  = "Имя"
    override def sEmail     = "Email (опционально, только для аватара)"
    override def sEnterGame = "Войти в игру"

    override def sShareLink = "Поделитесь ссылкой с другом для начала игры"
  }
}