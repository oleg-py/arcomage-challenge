package ac.game.cards.dsl

import ac.game.player.{CardScope, Player}
import cats.Order
import cats.implicits._
import cats.kernel.Comparison
import shapeless._
import shapeless.ops.coproduct.Reify

object Particles {
  case class Target(element: ParticleElement, side: structure.Target, decrease: Boolean)
  def select(side: structure.Target) = side match {
    case structure.Player => (_: CardScope).stats
    case structure.Enemy  => (_: CardScope).enemy
  }

  sealed abstract class ParticleElement(val diffOn: Order[Player])
  case object Wall      extends ParticleElement(Order.by(_.buildings.wall.value))
  case object Tower     extends ParticleElement(Order.by(_.buildings.tower.value))
  case object Bricks    extends ParticleElement(Order.whenEqual(
    Order.by(_.resources.bricks.value),
    Order.by(_.income.bricks.value)
  ))
  case object Gems      extends ParticleElement(Order.whenEqual(
    Order.by(_.resources.gems.value),
    Order.by(_.income.gems.value)
  ))
  case object Recruits extends ParticleElement(Order.whenEqual(
    Order.by(_.resources.recruits.value),
    Order.by(_.income.recruits.value)
  ))

  /*_*/
  val allElems: List[ParticleElement] =
    Reify[the.`Generic[ParticleElement]`.Repr].apply().toList
  /*_*/

  def getTargets(old: CardScope, next: CardScope): List[Target] = {
    for {
      el <- allElems
      tg <- List(structure.Player, structure.Enemy)
      s  = select(tg)
      c = el.diffOn.comparison(s(old), s(next))
      if c != Comparison.EqualTo
    } yield Target(el, tg, c == Comparison.GreaterThan)
  }
}
