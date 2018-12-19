package ac.frontend.components

import ac.game.cards.Card
import ac.game.cards.Card.Color
import eu.timepit.refined.types.numeric.{NonNegInt, PosInt}
import slinky.core.StatelessComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html.{className, div, span, title}


@react class ResourceBox extends StatelessComponent {
  case class Props(tpe: Card.Color, res: NonNegInt, income: PosInt, max: PosInt)

  def render(): ReactElement = {
    val (incomeName, amountName) = props.tpe match {
      case Color.Red => ("Quarry","Bricks")
      case Color.Blue => ("Magic", "Gems")
      case Color.Green => ("Dungeon", "Recruits")
    }
    val colorClass = props.tpe.lowerName

    div(className := s"resource-box $colorClass")(
      div(className := "income", title := incomeName)(props.income.toString),
      div(className := "amount", title := amountName)(
        span(props.res.toString),
        span(className := "additional-info")(s"/${props.max}")
      )
    )
  }
}
