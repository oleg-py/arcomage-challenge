package ac.frontend.components

import ac.game.player.CardScope
import eu.timepit.refined.types.numeric.PosInt
import slinky.core.StatelessComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import com.olegpy.shironeko.internals.SlinkyHotLoadingWorkaround._
import slinky.web.html.{className, div}

@react class Castles extends StatelessComponent {
  case class Props(cs: CardScope, maxTower: PosInt)

  def render(): ReactElement = {
    val Props(cs, maxTower) = props

    div(className := "castles")(
      div(className := "tower mine")(s"${cs.stats.buildings.tower} / $maxTower"),
      div(className := "wall mine")(s"${cs.stats.buildings.wall}"),
      div(className := "spacer"),
      div(className := "wall enemy")(s"${cs.enemy.buildings.wall}"),
      div(className := "tower enemy")(s"${cs.enemy.buildings.tower} / $maxTower")
    )
  }
}
