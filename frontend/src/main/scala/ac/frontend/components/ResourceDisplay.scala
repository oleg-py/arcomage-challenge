package ac.frontend.components

import ac.game.cards.Card.Color
import ac.game.player.Player
import slinky.core.StatelessComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html.{className, div}

@react class ResourceDisplay extends StatelessComponent {
  type Props = Player

  def render(): ReactElement = {
    val p = props
    div(className := "resources-display")(
      ResourceBox(Color.Red, p.resources.bricks, p.income.bricks),
      ResourceBox(Color.Blue, p.resources.gems, p.income.gems),
      ResourceBox(Color.Green, p.resources.recruits, p.income.recruits)
    )
  }
}

