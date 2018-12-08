package ac.frontend.components

import ac.frontend.Store
import ac.frontend.i18n.Lang
import ac.frontend.states.AnimatedCard
import slinky.core.facade.ReactElement
import slinky.web.html._
import ac.frontend.utils.StreamOps

object CardAnimation extends Store.Container(
  Store.animate.state withLatestFrom Store.locale.listen
) {
  def render(a: (Option[AnimatedCard], Lang)): ReactElement = a._1.map {
    // TODO beautifully flow
    case AnimatedCard(_, card, _, isDiscarded) =>
      div(className := "animation-overlay")(
        CardDisplay(card, a._2)
      )
  }
}
