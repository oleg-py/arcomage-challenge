package ac.webapp.react

import ac.game.player.Buildings
import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.vdom.html_<^._

case class WallAndTower (
  cls: String,
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

      <.div(
        ^.cls := s"buildings ${props.cls}",
        <.div(
          ^.cls := "tower-block"
        ),
        <.div(
          ^.cls := "tower-label",
          tower.toString
        ),
        <.div(
          ^.cls := "wall-block"
        ),
        <.div(
          ^.cls := "wall-label",
          wall.toString
        )
      )
    }
    .build
}
