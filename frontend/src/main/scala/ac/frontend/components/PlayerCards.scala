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
import slinky.core.StatelessComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.SyntheticMouseEvent
import slinky.web.html._

// TODO - ContainerNoState is broken
@react class PlayerCards extends StatelessComponent {
  case class Props(
    cards: Vector[Card],
    resources: Resources[NonNegInt],
    disableAll: Boolean,
    handleClick: (Props, Int) => SyntheticMouseEvent[div.tag.RefType] => Unit
  )



  def render: ReactElement = {
    div(className := "hand")(
      props.cards.zipWithIndex.map { case (card, i) =>
        CardDisplay(
          card,
          Some("disabled").filter(_ =>
            props.disableAll || !card.canPlayWith(props.resources)),
          props.handleClick(props, i)
        ).withKey(i.toString)
      }
    )
  }
}
