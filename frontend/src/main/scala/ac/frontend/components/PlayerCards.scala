package ac.frontend.components

import ac.frontend.Store
import ac.frontend.actions.card
import ac.frontend.i18n.Lang
import ac.game.Resources
import ac.game.cards.Card
import eu.timepit.refined.api.Refined
import eu.timepit.refined.types.numeric.NonNegInt
import slinky.core.StatelessComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.SyntheticMouseEvent
import slinky.web.html._


@react class PlayerCards extends StatelessComponent {
  case class Props(
    lang: Lang,
    cards: Vector[Card],
    resources: Resources[NonNegInt],
    disableAll: Boolean
  )

  private def handleClick(i: Int)(e: SyntheticMouseEvent[div.tag.RefType]): Unit = Store.execS { implicit alg =>
    e.preventDefault()
    e.stopPropagation()
    e.button match {
      case _ if props.disableAll =>
        alg.unit
      case 0 | 1 if props.cards(i).canPlayWith(props.resources) =>
        card.play(Refined.unsafeApply(i))
      case 2 =>
        card.discard(Refined.unsafeApply(i))
      case _ =>
        alg.unit
    }
  }

  def render(): ReactElement = div(
    className := "hand",
  )(
    props.cards.zipWithIndex.map { case (card, i) =>
      CardDisplay(
        card,
        props.lang,
        Some("disabled").filter(_ =>
          props.disableAll || !card.canPlayWith(props.resources)),
        handleClick(i)
      ).withKey(i.toString)
    }
  )
}
