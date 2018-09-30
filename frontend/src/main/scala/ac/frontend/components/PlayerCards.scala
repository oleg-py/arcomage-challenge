package ac.frontend.components

import ac.frontend.{CardData, Store}
import ac.frontend.actions.card
import ac.frontend.i18n.English
import ac.game.Resources
import ac.game.cards.Card
import cats.effect.IO
import eu.timepit.refined.api.Refined
import eu.timepit.refined.types.numeric.NonNegInt
import org.scalajs.dom.MouseEvent
import scala.scalajs.js.Dynamic.literal
import slinky.core.StatelessComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html._
import com.olegpy.shironeko.internals.SlinkyHotLoadingWorkaround._


@react class PlayerCards extends StatelessComponent {
  case class Props(cards: Vector[Card], resources: Resources[NonNegInt], myTurn: Boolean)

  private def handleClick(i: Int)(e: MouseEvent): Unit = Store.execS { implicit alg =>
    e.preventDefault()
    e.button match {
      case 0 | 1 if props.myTurn && props.cards(i).canPlayWith(props.resources) =>
        card.play(Refined.unsafeApply(i))
      case 2 if props.myTurn =>
        card.discard(Refined.unsafeApply(i))
      case _ =>
        IO.unit
    }
  }

  def render(): ReactElement = div(
    className := "hand"
  )(
    props.cards.zipWithIndex.map { case (card, i) =>
      val suffix = if (!card.canPlayWith(props.resources)) " disabled" else ""
      val Some(offsets) = CardData.find(_.name_en == card.name)
      div(
        key := i.toString,
        className := s"card ${card.color.toString.toLowerCase}" ++ suffix,
        onMouseDown := handleClick(i) _
      )(
        label(className := "card-name")(card.name),
        div(
          className := "image",
          style := literal(backgroundPosition =
            s"${offsets.offset_x * -175}px ${offsets.offset_y * - 104}px")
        ),
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
