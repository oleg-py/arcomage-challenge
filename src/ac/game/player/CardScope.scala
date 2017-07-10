package ac.game.player

import ac.game.cards.Card
import monocle.macros.Lenses

@Lenses case class CardScope (
  stats: Player,
  enemy: Player,
  turnMods: Vector[TurnMod]
) {
  def reverse: CardScope = copy(stats = enemy, enemy = stats)
  def applyReversed(f: Card.Fn) = f(reverse).reverse.norm
  def requireDiscard: Boolean = turnMods.headOption.contains(TurnMod.ForceDiscard)
  def passTurn: Boolean = turnMods.isEmpty
  def norm: CardScope = copy(stats.norm, enemy.norm)
}
