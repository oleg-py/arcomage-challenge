package ac.frontend.components

import ac.game.cards.Card
import slinky.core.StatelessComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html.div


@react class PlayerCards extends StatelessComponent {
  type Props = Vector[Card]

  def render(): ReactElement = div("<Cards>")
}
