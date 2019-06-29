package ac.frontend.components

import ac.frontend.Store
import ac.frontend.i18n._
import ac.frontend.states.{AnimatedCard, StoreAlg}
import slinky.core.facade.ReactElement
import slinky.web.html._
import ac.frontend.utils._
import com.olegpy.shironeko.util.combine

object CardAnimation extends Store.ContainerNoProps {
  case class State(maybeCard: Option[AnimatedCard], lang: Lang)

  def subscribe[F[_]: Subscribe]: fs2.Stream[F, State] =
    combine[State].from(
      StoreAlg[F].animate.state,
      StoreAlg[F].locale.discrete
    )

  def render[F[_]: Render](state: State): ReactElement = withLang { implicit lang =>
    state.maybeCard map {
      case AnimatedCard(card, enemy, isDiscarded) =>
        div(className := cls"animation-overlay ${enemy -> "enemy"}")(
          div(className := "card-overlay")(
            CardDisplay(card, overlay =
              if (isDiscarded) span(className := "discarded-text")(
                Tr("Discarded", "Сброшена")
              )
              else None
            )
          )
        )
    }
  }
}
