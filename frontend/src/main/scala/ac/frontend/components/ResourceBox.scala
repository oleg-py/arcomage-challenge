package ac.frontend.components

import ac.game.cards.Card
import eu.timepit.refined.types.numeric.{NonNegInt, PosInt}
import slinky.core.StatelessComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html.{className, div, span}


@react class ResourceBox extends StatelessComponent {
  case class Props(tpe: Card.Color, res: NonNegInt, income: PosInt, max: PosInt)

  def render(): ReactElement = {
    val colorClass = props.tpe.lowerName

    div(className := s"resource-box $colorClass")(
      div(className := "income")(props.income.toString),
      div(className := "amount")(
        span(props.res.toString),
        span(className := "additional-info")(s"/${props.max}")
      )
    )
  }
}
