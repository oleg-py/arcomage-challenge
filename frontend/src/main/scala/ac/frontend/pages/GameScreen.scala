package ac.frontend.pages

import ac.frontend.Store
import slinky.core.facade.ReactElement
import slinky.web.html.{className, div, onContextMenu}
import ac.frontend.utils.{StreamOps, combine}
import ac.frontend.components._
import ac.frontend.i18n.Lang
import ac.frontend.states.AppState.User
import ac.frontend.states.{Progress, StoreAlg}
import ac.game.cards.Card
import monix.eval.Task
import cats.implicits._

/*_*/
object GameScreen extends Store.ContainerNoProps {
  case class State(
    p: Progress,
    me: User,
    enemy: User,
    hand: Vector[Card],
    lang: Lang,
    canMove: Boolean
  )


  def subscribe(implicit F: StoreAlg[Task]): fs2.Stream[Task, State] =
    combine[State].from(
      F.game.listen,
      F.me.listen.unNone[User],
      F.enemy.listen.unNone[User],
      F.cards.listen,
      F.locale.listen,
      F.myTurn.listen
        .withLatestFrom(F.animate.state.map(_.isEmpty))
        .map { case (a, b) => a && b }
    )


  def render(state: State)(implicit F: StoreAlg[Task]): ReactElement = {
    val State(Progress(st, conds), me, enemy, cards, lang, canMove) =
      state

    val maxRes = conds.resources
    div(className := "field", onContextMenu := { _.preventDefault() })(
      div(className := "game-screen")(
        div(className := "battlefield")(
          div(className := "stats mine")(
            PlayerDisplay(me),
            ResourceDisplay(st.stats, maxRes)
          ),
          div(className := "history-overlayed")(
            HistoryDisplay(),
            Castles(st, conds.tower)
          ),
          div(className := "stats enemy")(
            PlayerDisplay(enemy),
            ResourceDisplay(st.enemy, maxRes)
          )
        ),
        Notice(),
        PlayerCards(PlayerCards.Props(lang, cards, st.stats.resources, !canMove))
      ),
      CardAnimation(),
      ReconnectingOverlay(),
    )
  }
}
