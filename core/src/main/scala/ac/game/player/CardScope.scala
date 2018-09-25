package ac.game.player

import ac.game.Resources
import ac.game.cards.Card
import monocle.macros.Lenses
import eu.timepit.refined.auto._

@Lenses case class CardScope (
  stats: Player,
  enemy: Player,
  turnMods: Vector[TurnMod]
) {
  def reverse: CardScope = copy(stats = enemy, enemy = stats)
  def applyReversed(f: Card.Fn): CardScope = f(reverse).reverse
  def requireDiscard: Boolean = turnMods.headOption.contains(TurnMod.ForceDiscard)
  def passTurn: Boolean = turnMods.isEmpty
}

object CardScope {
  val Dummy: CardScope = {
    val dummyPlayer = Player(
      Buildings(0, 0),
      Resources.all(0),
      Resources.all(1)
    )

    CardScope(dummyPlayer, dummyPlayer, Vector())
  }
}
