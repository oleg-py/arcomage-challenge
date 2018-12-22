package ac.frontend.components

import ac.frontend.Store
import ac.frontend.actions.matches
import ac.frontend.states.{AppState, RematchState}
import ac.frontend.states.AppState.{Defeat, Victory}
import slinky.core.facade.ReactElement
import slinky.web.html._
import ac.frontend.utils.StreamOps

object EndgameNotice extends Store.Container(
  Store.app.listen withLatestFrom
  Store.rematchState.listen
) {
  def render(a: (AppState, RematchState)): ReactElement = {
    val (as, rs) = a

    val runRematch = () => Store.execS { implicit alg =>
      matches.proposeRematch
    }

    as match {
      case Victory | Defeat =>
        div(className := "endgame-notice")(
          h1(if (as == Victory) "You win!" else "You've lost"),
          rs match {
            case RematchState.NotAsked =>
              button(onClick := runRematch)("Rematch")
            case RematchState.Asked =>
              button(onClick := runRematch)("Agree to rematch")
            case _ =>
              div("Waiting...")
          }
        )
      case _ => None
    }
  }
}
