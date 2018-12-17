package ac.frontend.states

import ac.game.cards.Card


case class AnimatedCard (
  card: Card,
  isEnemy: Boolean,
  isDiscarded: Boolean
)
