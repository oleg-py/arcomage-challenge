package ac.frontend.pages

import ac.frontend.Store
import slinky.core.facade.ReactElement
import slinky.web.html.{className, div}
import ac.frontend.utils.StreamOps
import Store.implicits._
import ac.frontend.components._
import ac.frontend.i18n.Lang
import ac.frontend.states.AppState.User
import ac.frontend.states.Progress
import ac.game.cards.Card

/*_*/
object GameScreen extends Store.Container(
  Store.game.listen
    .withLatestFrom(Store.me.listen.unNone)
    .withLatestFrom(Store.enemy.listen.unNone)
    .withLatestFrom(Store.cards.listen)
    .withLatestFrom(Store.locale.listen)
    .withLatestFrom {
      Store.myTurn.listen
        .withLatestFrom(Store.animate.state.map(_.isEmpty))
        .map { case (a, b) => a && b }
    }
    .frameDebounced
) {
  def render(a: (Progress, User, User, Vector[Card], Lang, Boolean)): ReactElement = {
    val (Progress(state, conds), me, enemy, cards, lang, canMove) = a
    div(className := "field")(
      div(className := "game-screen")(
        div(className := "stats mine")(
          PlayerDisplay(me),
          ResourceDisplay(state.stats)
        ),
        div(className := "battlefield")(
          DummyCards(),
          Castles(state, conds.tower),
          PlayerCards(lang, cards, state.stats.resources, !canMove)
        ),
        div(className := "stats enemy")(
          PlayerDisplay(enemy),
          ResourceDisplay(state.enemy)
        )
      ),
      CardAnimation()
    )
  }
}
