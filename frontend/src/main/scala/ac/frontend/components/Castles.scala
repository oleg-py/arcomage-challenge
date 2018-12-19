package ac.frontend.components

import scala.scalajs.js.Dynamic.{literal => js}

import ac.game.player.{CardScope, Player}
import eu.timepit.refined.types.numeric.PosInt
import slinky.core.StatelessComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html._

@react class Castles extends StatelessComponent {
  case class Props(cs: CardScope, maxTower: PosInt)

  private val MaxTowerHeight = 340

  private val towerHeight = (_: Player).buildings.tower.value.toDouble /
                              props.maxTower.value * MaxTowerHeight

  private val wallHeight = (_: Player).buildings.wall.value.toDouble /
                              props.maxTower.value * 0.75 * MaxTowerHeight

  private def blockOf(height: Double): ReactElement = {
    div(className := "block", style := js(height = s"${Math.ceil(height)}px"))
  }

  def render(): ReactElement = {
    val cs = props.cs

    def infoSpan = span(className := "additional-info")(s"/${props.maxTower}")

    div(className := "castles")(
      div(className := "tower mine")(
        div(className := "capstone"),
        blockOf(towerHeight(cs.stats)),
        label(
          span(s"${cs.stats.buildings.tower}"),
          infoSpan
        )),
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
        div(className := "capstone"),
        blockOf(towerHeight(cs.enemy)),
        label(
          span(s"${cs.enemy.buildings.tower}"),
          infoSpan
        ),
      )
    )
  }
}
