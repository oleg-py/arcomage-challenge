package ac.frontend.components

import ac.frontend.Store
import ac.frontend.actions.card
import ac.frontend.states.StoreAlg
import ac.game.Resources
import ac.game.cards.Card
import cats.effect.Sync
import com.olegpy.shironeko.interop.Exec
import eu.timepit.refined.api.Refined
import eu.timepit.refined.types.numeric.NonNegInt
import slinky.core.facade.ReactElement
import slinky.web.SyntheticMouseEvent
import slinky.web.html._


object PlayerCards extends Store.ContainerNoState {
  case class Props(
    cards: Vector[Card],
    resources: Resources[NonNegInt],
    disableAll: Boolean
  )

  // TODO - factor out cards? refactor ton of constraints?
  private def handleClick[F[_]](
    props: Props, i: Int)(
    e: SyntheticMouseEvent[div.tag.RefType])(
    implicit F: Sync[F],
    store: StoreAlg[F],
    ex: Exec[F]
  ): Unit = exec(F.suspend {
    e.preventDefault()
    e.stopPropagation()
    e.button match {
      case _ if props.disableAll =>
        F.unit
      case 0 | 1 if props.cards(i).canPlayWith(props.resources) =>
        card.play[F](Refined.unsafeApply(i))
      case 2 =>
        card.discard[F](Refined.unsafeApply(i))
      case _ =>
        F.unit
    }
  })

  def render[F[_]: Render](props: Props): ReactElement =
    div(className := "hand")(
      props.cards.zipWithIndex.map { case (card, i) =>
        CardDisplay(
          card,
          Some("disabled").filter(_ =>
            props.disableAll || !card.canPlayWith(props.resources)),
          handleClick[F](props, i)
        ).withKey(i.toString)
      }
    )
}
