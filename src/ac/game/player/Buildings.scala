package ac.game.player

case class Buildings (
  tower: Int,
  wall:  Int
) {
  def receiveDamage(am: Int): Buildings = {
    val wallDamage = Math.min(wall, am)
    val towerDamage = am - wallDamage
    copy(tower - towerDamage, wall - wallDamage)
  }
}
