package ac.model

import monocle.macros.Lenses

@Lenses case class State (
  stats: Player,
  enemy: Player,
  turnModifiers: Vector[TurnMod]
)
