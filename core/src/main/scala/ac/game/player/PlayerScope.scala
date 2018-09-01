package ac.game.player

import ac.game.GameConditions
import ac.game.cards.Cards
import monocle.macros.Lenses


@Lenses case class PlayerScope (
  playerName: String,
  enemyName: String,
  cards: Cards,
  game: CardScope,
  conditions: GameConditions
) {
  def isVictory = conditions.isVictory(game)
  def isDefeat =  conditions.isVictory(game.reverse)

  def canPlay(n: Int) = !game.requireDiscard && (-cards.hand(n).cost all_<= game.stats.resources)
}
