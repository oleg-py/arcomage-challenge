package ac.frontend.states

import ac.game.cards.Card


case class AnimatedCard (
  state: AnimatedCard.AnimState,
  card: Card,
  isEnemy: Boolean,
  isDiscarded: Boolean
)

object AnimatedCard {
  sealed trait AnimState
  case object FadeIn  extends AnimState
  case object Show    extends AnimState
  case object FadeOut extends AnimState
}
