package ac.frontend.states

import ac.game.cards.Card
import cats.data.Chain


case class History(nPlayed: Int, current: Chain[(Card, Boolean)], mine: Boolean) {
  def >->(anim: AnimatedCard): History =
    if (mine == anim.isEnemy) {
      History(nPlayed, current :+ (anim.card -> anim.isDiscarded), mine)
    } else {
      History(nPlayed + current.length.toInt, Chain.one(anim.card -> anim.isDiscarded), !mine)
    }
}
