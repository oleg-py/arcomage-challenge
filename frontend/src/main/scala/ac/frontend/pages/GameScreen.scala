package ac.frontend.pages

import ac.frontend.Store
import slinky.core.facade.ReactElement
import slinky.web.html.{className, div, onContextMenu}
import ac.frontend.components._
import ac.frontend.states.AppState.User
import ac.frontend.states.Progress
import ac.game.cards.Card
import com.olegpy.shironeko.util._

/*_*/
object GameScreen extends Store.ContainerNoProps {
  case class State(
    p: Progress,
    me: User,
    enemy: User,
    hand: Vector[Card],
    canMove: Boolean
  )

  def render[F[_]: Render](state: State): ReactElement = {
    val State(Progress(st, conds), me, enemy, cards, canMove) =
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
        PlayerCards(PlayerCards.Props(cards, st.stats.resources, !canMove))
      ),
      CardAnimation(),
      ReconnectingOverlay(),
    )
  }

  def subscribe[F[_]: Subscribe]: fs2.Stream[F, State] = {
    val F = getAlgebra
    val canMove =
      combine[(Boolean, Boolean)]
        .from(
          F.myTurn.discrete,
          F.animate.state.map(_.isEmpty)
        )
        .map { case (a, b) => a && b }

    combine[State].from(
      F.game.discrete,
      F.me.discrete.unNone,
      F.enemy.discrete.unNone,
      F.cards.discrete,
      canMove
    )
  }
}
