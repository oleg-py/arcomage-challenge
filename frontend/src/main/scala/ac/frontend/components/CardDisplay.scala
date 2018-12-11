package ac.frontend.components

import scala.scalajs.js.Dynamic.literal

import ac.frontend.CardData
import ac.frontend.i18n.Lang
import ac.game.cards.Card
import org.scalajs.dom.raw.MouseEvent
import slinky.core.StatelessComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html._


@react class CardDisplay extends StatelessComponent {
  case class Props(
    card: Card,
    lang: Lang,
    className: Option[String] = None,
    onClick: MouseEvent => Unit = _ => {}
  )

  def render(): ReactElement = {
    val Props(card, lang, customClass, onClick) = props
    val Some(offsets) = CardData.find(_.name_en == card.name)

    div(
      className := (s"card ${card.color.toString.toLowerCase}" ++ customClass.fold("")(" " ++ _)),
      onMouseDown := onClick
    )(
      label(className := "card-name")(lang.cardName(card.name)),
      div(
        className := "image",
        style := literal(backgroundPosition =
          s"${offsets.offset_x * -128}px ${offsets.offset_y * -76}px"),
      ),
      div(className := "description")(
        lang.cardDescription(card.effect)
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
}

object CardDisplay
