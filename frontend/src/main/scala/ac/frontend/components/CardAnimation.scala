package ac.frontend.components

import ac.frontend.Store
import ac.frontend.i18n.Lang
import ac.frontend.states.{AnimatedCard, StoreAlg}
import slinky.core.facade.ReactElement
import slinky.web.html._
import ac.frontend.utils._
import monix.eval.Task
import cats.implicits._

object CardAnimation extends Store.ContainerNoProps {
  case class State(maybeCard: Option[AnimatedCard], lang: Lang)

  def subscribe(implicit F: StoreAlg[Task]): fs2.Stream[Task, State] =
    combine[State].from(F.animate.state, F.locale.listen)


  def render(state: State)(implicit F: StoreAlg[Task]): ReactElement =
    state.maybeCard map {
      case AnimatedCard(card, enemy, isDiscarded) =>
        div(className := cls"animation-overlay ${enemy -> "enemy"}")(
          div(className := "card-overlay")(
            CardDisplay(card, state.lang, overlay =
              if (isDiscarded) span(className := "discarded-text")("Discarded")
              else None
            )
          )
        )
    }
}
