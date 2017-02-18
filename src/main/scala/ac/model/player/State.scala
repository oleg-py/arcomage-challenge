package ac.model.player

import monocle.macros.Lenses

@Lenses case class State (
  stats: Player,
  enemy: Player,
  turnModifiers: Vector[TurnMod]
) {
  def reverse: State = copy(stats = enemy, enemy = stats)
}
