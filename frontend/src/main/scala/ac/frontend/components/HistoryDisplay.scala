package ac.frontend.components

import ac.frontend.i18n.Lang
import ac.frontend.Store
import slinky.core.facade.ReactElement
import slinky.web.html.{className, div, key}


object HistoryDisplay extends Store.Container(Store.cardHistory) {
  def render(a: Store.History): ReactElement = {
    val Store.History(nPlayed, cards, _) = a

    val cc = Vector.fill(nPlayed min 3)(None) ++ cards.toVector.map(Some(_))

    div(className := "card-history")(
      cc.takeRight(5).zipWithIndex.map {
        case (None, idx) =>
          div(key := idx.toString, className := "card empty")("")
        case (Some((card, isDiscarded)), idx) =>
          CardDisplay(card, Lang.En).withKey(idx.toString): ReactElement
      }
    )
  }
}
