package ac.webapp.react

import ac.game.player.Buildings
import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.vdom.html_<^._


case class BuildingsDisplay (player: Buildings, enemy: Buildings, maxTower: Int) {
  def /> = BuildingsDisplay.Component(this)
}

object BuildingsDisplay {
  val Component = ScalaComponent.builder[BuildingsDisplay]("BuildingsDisplay")
    .render_P { props =>
      <.div(
        ^.cls := "buildings-display",
        WallAndTower("player", props.maxTower, props.player)./>,
        WallAndTower("enemy",  props.maxTower, props.enemy)./>
      )
    }
    .build
}
