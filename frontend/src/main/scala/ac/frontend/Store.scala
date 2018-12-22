package ac.frontend

import ac.frontend.states._
import ac.game.cards.Card
import cats.data.Chain
import cats.effect._
import com.olegpy.shironeko._
import fs2.Stream
import monix.eval.Task


//noinspection TypeAnnotation
object Store extends StoreBase(Main.Instance)
  with StoreAlg[Task] with SlinkyIntegration[Task] with ImpureIntegration[Task]
{
  override object implicits extends implicits {
    implicit def timer: Timer[Task] = Task.timer
    implicit def contextShift: ContextShift[Task] = Task.contextShift
  }

  case class History(nPlayed: Int, current: Chain[(Card, Boolean)], mine: Boolean) {
    def >->(anim: AnimatedCard) =
      if (mine == anim.isEnemy) {
        History(nPlayed, current :+ (anim.card -> anim.isDiscarded), mine)
      } else {
        History(nPlayed + current.length.toInt, Chain.one(anim.card -> anim.isDiscarded), !mine)
      }
  }

  def cardHistory: Stream[Task, History] =
    animate.state.unNone
      .debounce(animate.animDuration)
      .scan(History(0, Chain.empty, mine = true))(_ >-> _)
}
