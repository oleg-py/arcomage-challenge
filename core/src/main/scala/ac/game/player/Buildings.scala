package ac.game.player

import eu.timepit.refined.types.numeric.NonNegInt
import monocle.macros.Lenses


@Lenses case class Buildings (
  tower: NonNegInt,
  wall:  NonNegInt
) {
  def damageBy(amount: NonNegInt): Buildings = {
    val wallDamage = Math.min(wall.value, amount.value)
    val towerDamage = amount.value - wallDamage

    (tower.value - towerDamage, wall.value - wallDamage) match {
      case (NonNegInt(t), NonNegInt(w)) => Buildings(t, w)
      case _ => Buildings.empty
    }
  }
}

object Buildings {
  def empty: Buildings = Buildings(NonNegInt(0), NonNegInt(0))
}
