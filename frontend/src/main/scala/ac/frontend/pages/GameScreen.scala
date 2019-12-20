package ac.frontend.pages

import ac.frontend.Store
import ac.frontend.actions.card
import slinky.core.facade.ReactElement
import slinky.web.html.{className, div, onContextMenu}
import ac.frontend.components._
import ac.frontend.states.AppState.User
import ac.frontend.states.{Progress, StoreAlg}
import ac.game.cards.Card
import cats.effect.Sync
import com.olegpy.shironeko.interop.Exec
import com.olegpy.shironeko.util._
import eu.timepit.refined.api.Refined
import slinky.web.SyntheticMouseEvent

/*_*/
object GameScreen extends Store.ContainerNoProps {
  case class State(
    p: Progress,
    me: User,
    enemy: User,
    hand: Vector[Card],
    canMove: Boolean
  )


  // TODO - factor out cards? refactor ton of constraints?
  private def handleClick[F[_]](
    props: PlayerCards.Props, i: Int)(
    e: SyntheticMouseEvent[div.tag.RefType])(
    implicit F: Sync[F],
    store: StoreAlg[F],
    ex: Exec[F]
  ): Unit = exec(F.suspend {
    e.preventDefault()
    e.stopPropagation()
    e.button match {
      case _ if props.disableAll =>
        F.unit
      case 0 | 1 if props.cards(i).canPlayWith(props.resources) =>
        card.play[F](Refined.unsafeApply(i))
      case 2 =>
        card.discard[F](Refined.unsafeApply(i))
      case _ =>
        F.unit
    }
  })

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
        PlayerCards(cards, st.stats.resources, !canMove, handleClick[F] _)
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
