package ac.game.player

case class Buildings (
  tower: Int,
  wall:  Int
) {
  def damageBy(am: Int): Buildings = {
    val wallDamage = Math.min(wall, am)
    val towerDamage = am - wallDamage
    copy(tower - towerDamage, wall - wallDamage)
  }

  def norm = copy(tower max 0, wall max 0)
}
