package ac.frontend.components

import scala.scalajs.js.Dynamic.{literal => js}

import ac.game.player.{CardScope, Player}
import eu.timepit.refined.types.numeric.PosInt
import slinky.core.StatelessComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import com.olegpy.shironeko.internals.SlinkyHotLoadingWorkaround._
import slinky.web.html.{className, div, label, style}

@react class Castles extends StatelessComponent {
  case class Props(cs: CardScope, maxTower: PosInt)

  private val ViewportHeight = 500

  private val towerHeight = (_: Player).buildings.tower.value.toDouble /
                              props.maxTower.value * ViewportHeight

  private val wallHeight = (_: Player).buildings.wall.value.toDouble /
                              props.maxTower.value * 0.75 * ViewportHeight

  private def blockOf(height: Double): ReactElement = {
    div(className := "block", style := js(height = s"${Math.ceil(height)}px"))
  }

  def render(): ReactElement = {
    val Props(cs, maxTower) = props
    val myHeight = cs.stats.buildings.tower.value * 100 / maxTower.value
    val enemyHeight = cs.enemy.buildings.tower.value * 100 / maxTower.value

    div(className := "castles")(
      div(className := "tower mine")(
        blockOf(towerHeight(cs.stats)),
        label(s"${cs.stats.buildings.tower}")),
      div(className := "wall mine")(
        blockOf(wallHeight(cs.stats)),
        label(s"${cs.stats.buildings.wall}"),
      ),
      div(className := "spacer"),
      div(className := "wall enemy")(
        blockOf(wallHeight(cs.enemy)),
        label(s"${cs.enemy.buildings.wall}"),
      ),
      div(className := "tower enemy")(
        blockOf(towerHeight(cs.enemy)),
        label(s"${cs.enemy.buildings.tower}"),
      )
    )
  }
}
