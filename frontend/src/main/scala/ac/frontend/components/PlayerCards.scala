package ac.frontend.components

import ac.frontend.Store
import ac.frontend.actions.card
import ac.frontend.i18n.English
import ac.game.cards.Card
import cats.effect.IO
import eu.timepit.refined.api.Refined
import org.scalajs.dom.MouseEvent
import slinky.core.StatelessComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html._


@react class PlayerCards extends StatelessComponent {
  type Props = Vector[Card]

  private def handleClick(i: Int)(e: MouseEvent): Unit = Store.execS { implicit alg =>
    e.button match {
      case 0 | 1 =>
        card.play(Refined.unsafeApply(i))
      case 2 =>
        e.preventDefault()
        card.discard(Refined.unsafeApply(i))
      case _ =>
        IO.unit
    }
  }

  def render(): ReactElement = div(
    className := "hand"
  )(
    props.zipWithIndex.map { case (card, i) =>
      div(
        key := i.toString,
        className := s"card ${card.name} ${card.color.toString.toLowerCase}",
        onMouseDown := handleClick(i) _
      )(
        label(className := "card-name")(card.name),
        div(className := "image"),
        div(className := "description")(
          English.cardDescription(card.effect)
            .toList
            .zipWithIndex
            .map { case (line, j) =>
              p(key := j.toString)(line)
            }
        ),
        div(className := "footer")(
          div(className := "worth")(card.worth.toString)
        )
      )
    }
  )
}
