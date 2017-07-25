package ac.webapp.react

import ac.game.player.Buildings
import ReactSyntax._

case class WallAndTower (
  `class`: String,
  maxTower: Int,
  buildings: Buildings
) {
  def /> = WallAndTower.Component(this)
}

object WallAndTower {
  val Component = ScalaComponent.builder[WallAndTower]("PlayerBuildings")
    .render_P { props =>
      val Buildings(wall, tower) = props.buildings
//      val towerRatio = (100.0 * tower / props.maxTower).toInt max 100
//      val wallRatio  = (75.0  * wall  / props.maxTower).toInt max 75

      div(`class` := s"buildings ${props.`class`}")(
        div(`class` := "tower-block"),
        div(`class` := "tower-label", tower.toString),
        div(`class` := "wall-block"),
        div(`class` := "wall-label", wall.toString)
      )
    }
    .build
}
