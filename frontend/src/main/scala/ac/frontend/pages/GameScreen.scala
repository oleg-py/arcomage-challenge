package ac.frontend.pages

import ac.frontend.Store
import slinky.core.facade.ReactElement
import slinky.web.html.{className, div}
import ac.frontend.utils.StreamOps
import Store.implicits._
import ac.frontend.components._
import ac.frontend.states.AppState.User
import ac.frontend.states.Progress
import ac.game.cards.Card

object GameScreen extends Store.Container(
  Store.game.listen
    .withLatestFrom(Store.me.listen.unNone)
    .withLatestFrom(Store.enemy.listen.unNone)
    .withLatestFrom(Store.cards.listen)
    .map { case (((a, b), c), d) => (a, b, c, d) }
) {
  def render(a: (Progress, User, User, Vector[Card])): ReactElement = {
    val (Progress(state, conds), me, enemy, cards) = a
    div(className := "game-screen")(
      div(className := "stats mine")(
        PlayerDisplay(me),
        ResourceDisplay(state.stats)
      ),
      div(className := "battlefield")(
        DummyCards(),
        Castles(state, conds.tower),
        PlayerCards(cards, state.stats.resources, true)
      ),
      div(className := "stats enemy")(
        PlayerDisplay(enemy),
        ResourceDisplay(state.enemy)
      )
    )
  }
}
