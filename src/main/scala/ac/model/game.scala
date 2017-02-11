package ac.model

import monocle.macros.Lenses

case class Player (
  buildings : Buildings,
  resources : Resources,
  income    : Resources
)

@Lenses case class State (
  cards: Cards,
  stats: Player,
  enemy: Player,
  turnModifiers: Vector[TurnMod]
)

sealed trait TurnMod
case object PlayAgain extends TurnMod
case object ForceDiscard extends TurnMod

case class Cards (
  hand: List[Card],
  deck: List[Card],
  used: List[Card]
)
