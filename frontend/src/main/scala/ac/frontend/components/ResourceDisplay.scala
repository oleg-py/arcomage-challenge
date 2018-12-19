package ac.frontend.components

import ac.game.cards.Card.Color
import ac.game.player.Player
import eu.timepit.refined.types.numeric.PosInt
import slinky.core.StatelessComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html.{className, div}

@react class ResourceDisplay extends StatelessComponent {
  case class Props(p: Player, maxResource: PosInt)

  def render(): ReactElement = {
    val Props(p, max) = props
    div(className := "resources-display")(
      ResourceBox(Color.Red, p.resources.bricks, p.income.bricks, max),
      ResourceBox(Color.Blue, p.resources.gems, p.income.gems, max),
      ResourceBox(Color.Green, p.resources.recruits, p.income.recruits, max)
    )
  }
}

