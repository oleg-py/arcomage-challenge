package ac.game
package player

import monocle.macros.Lenses

@Lenses case class Player (
  buildings : Buildings,
  resources : Resources,
  income    : Resources
) {
  def norm = Player(
    buildings.norm,
    resources atLeast 0,
    income    atLeast 1
  )

  def addResources(r: Resources = income) = copy(resources = resources + r)
}
