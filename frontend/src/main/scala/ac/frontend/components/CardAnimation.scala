package ac.frontend.components

import ac.frontend.Store
import ac.frontend.i18n.Lang
import ac.frontend.states.AnimatedCard
import slinky.core.facade.ReactElement
import slinky.web.html._
import ac.frontend.utils._

object CardAnimation extends Store.Container(
  Store.animate.state withLatestFrom Store.locale.listen
) {
  def render(a: (Option[AnimatedCard], Lang)): ReactElement = a._1.map {
    case AnimatedCard(card, enemy, isDiscarded) =>
      div(className := cls"animation-overlay ${enemy -> "enemy"}")(
        div(className := "card-overlay")(
          CardDisplay(card, a._2, overlay =
            if (isDiscarded) span(className := "discarded-text")("Discarded")
            else None
          )
        )
      )
  }
}
