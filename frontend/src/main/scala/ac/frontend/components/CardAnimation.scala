package ac.frontend.components

import ac.frontend.Store
import ac.frontend.i18n.Lang
import ac.frontend.states.{AnimatedCard, StoreAlg}
import slinky.core.facade.ReactElement
import slinky.web.html._
import ac.frontend.utils._
import cats.effect.Concurrent
import com.olegpy.shironeko.interop.Exec
import com.olegpy.shironeko.util.combine

object CardAnimation extends Store.ContainerNoProps {
  case class State(maybeCard: Option[AnimatedCard], lang: Lang)

  def subscribe[F[_]: Concurrent: StoreAlg]: fs2.Stream[F, State] =
    combine[State].from(
      StoreAlg[F].animate.state,
      StoreAlg[F].locale.discrete
    )

  def render[F[_]: Concurrent: StoreAlg: Exec](state: State): ReactElement =
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
