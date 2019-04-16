package ac.frontend.pages

import ac.frontend.Store
import slinky.core.facade.ReactElement
import slinky.web.html.{className, div, onContextMenu}
import ac.frontend.components._
import ac.frontend.i18n.Lang
import ac.frontend.states.AppState.User
import ac.frontend.states.{Progress, StoreAlg}
import ac.game.cards.Card
import cats.effect.Concurrent
import monix.eval.Task
import cats.implicits._
import com.olegpy.shironeko.interop.Exec
import com.olegpy.shironeko.util._
import slinky.web.SyntheticMouseEvent

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


  def render[F[_]: Concurrent: StoreAlg: Exec](state: State): ReactElement = {
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

  def subscribe[F[_]: Concurrent](implicit F: StoreAlg[F]): fs2.Stream[F, State] =
    combine[State].fromStreams(
      F.game.discrete,
      F.me.discrete.unNone[User],
      F.enemy.discrete.unNone[User],
      F.cards.discrete,
      F.locale.discrete,
      combine[(Boolean, Boolean)].fromStreams(
        F.myTurn.discrete,
        F.animate.state.map(_.isEmpty)
      ).map { case (a, b) => a && b }
    )
}
