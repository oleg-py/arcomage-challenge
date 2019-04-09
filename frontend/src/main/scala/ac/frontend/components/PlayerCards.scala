package ac.frontend.components

import ac.frontend.Store
import ac.frontend.actions.card
import ac.frontend.i18n.Lang
import ac.frontend.states.StoreAlg
import ac.game.Resources
import ac.game.cards.Card
import eu.timepit.refined.api.Refined
import eu.timepit.refined.types.numeric.NonNegInt
import monix.eval.Task
import slinky.core.facade.ReactElement
import slinky.web.SyntheticMouseEvent
import slinky.web.html._


object PlayerCards extends Store.ContainerNoState {
  case class Props(
    lang: Lang,
    cards: Vector[Card],
    resources: Resources[NonNegInt],
    disableAll: Boolean
  )

  // TODO - factor out cards?
  private def handleClick(
    props: Props, i: Int)(
    e: SyntheticMouseEvent[div.tag.RefType])(
    implicit
    store: StoreAlg[Task]
  ): Unit = exec(Task.suspend {
    e.preventDefault()
    e.stopPropagation()
    e.button match {
      case _ if props.disableAll =>
        Task.unit
      case 0 | 1 if props.cards(i).canPlayWith(props.resources) =>
        card.play(Refined.unsafeApply(i))
      case 2 =>
        card.discard(Refined.unsafeApply(i))
      case _ =>
        Task.unit
    }
  })


  def render(props: Props)(implicit F: StoreAlg[Task]): ReactElement = div(
    className := "hand",
  )(
    props.cards.zipWithIndex.map { case (card, i) =>
      CardDisplay(
        card,
        props.lang,
        Some("disabled").filter(_ =>
          props.disableAll || !card.canPlayWith(props.resources)),
        handleClick(props, i)
      ).withKey(i.toString)
    }
  )
}
