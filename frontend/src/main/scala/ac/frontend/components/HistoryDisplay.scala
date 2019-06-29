package ac.frontend.components

import ac.frontend.i18n.Lang
import ac.frontend.Store
import ac.frontend.states.{History, StoreAlg}
import slinky.core.facade.ReactElement
import slinky.web.html._


object HistoryDisplay extends Store.ContainerNoProps {
  type State = History // TODO - consider inlining

  def subscribe[F[_]: Subscribe]: fs2.Stream[F, History] =
    StoreAlg[F].cardHistory // TODO - consider inlining

  def render[F[_]: Render](state: History): ReactElement = {
    val History(nPlayed, cards, _) = state

    val cc = Vector.fill(nPlayed min 3)(None) ++ cards.toVector.map(Some(_))

    div(className := "card-history")(
      cc.takeRight(5).zipWithIndex.map {
        case (None, idx) =>
          div(key := idx.toString, className := "card empty")("")
        case (Some((card, isDiscarded)), idx) =>
          val overlay: ReactElement =
            if (isDiscarded) span(className := "discarded-text")("Discarded")
            else None
          CardDisplay(card, Lang.En, overlay = overlay).withKey(idx.toString): ReactElement
      }
    )
  }
}