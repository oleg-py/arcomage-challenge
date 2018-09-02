package ac.game
package player

import eu.timepit.refined.types.numeric.{NonNegInt, PosInt}
import monocle.macros.Lenses

@Lenses case class Player (
  buildings : Buildings,
  resources : Resources[NonNegInt],
  income    : Resources[PosInt]
) {
  def addResources[N](r: Resources[N]): Player = {
    val rs = r + resources
    copy(resources = rs.min_0)
  }

  def receiveIncome: Player = addResources(income)
}
