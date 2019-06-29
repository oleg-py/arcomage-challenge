package ac.frontend.components

import scala.scalajs.js.Dynamic.literal

import ac.frontend.CardData
import ac.frontend.i18n._
import ac.game.cards.Card
import slinky.core.StatelessComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.SyntheticMouseEvent
import slinky.web.html._


@react class CardDisplay extends StatelessComponent {
  case class Props(
    card: Card,
    className: Option[String] = None,
    onClick: SyntheticMouseEvent[div.tag.RefType] => Unit = _ => {},
    overlay: ReactElement = None,
  )

  private def describe(card: Card)(implicit lang: Lang): List[String] =
    CardData
      .find(_.name_en == card.name)
      .flatMap(card => card.customDescription.in(lang).toOption)
      .filter(_.nonEmpty)
      .map(_.split('|').toList)
      .getOrElse {
        lang.cardDescription(card.effect).toList
      }

  def render(): ReactElement = withLang { implicit lang =>
    val Props(card, customClass, onClick, overlay) = props
    val Some(data) = CardData.find(_.name_en == card.name)
    val (x, y) = data.spriteOffsets

    div(
      className := (s"card ${card.color.toString.toLowerCase}" ++ customClass.fold("")(" " ++ _)),
      onMouseDown := onClick
    )(
      label(className := "card-name")(data.localizedName),
      div(
        className := "image",
        style := literal(backgroundPosition =
          s"${x * -128}px ${y * -76}px"),
      ),
      overlay,
      div(className := "description")(
        describe(card)
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
