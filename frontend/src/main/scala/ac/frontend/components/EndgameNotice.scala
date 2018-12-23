package ac.frontend.components

import ac.frontend.Store
import ac.frontend.actions.matches
import ac.frontend.states.{AppState, RematchState}
import ac.frontend.states.AppState.{Defeat, Victory}
import slinky.core.facade.ReactElement
import slinky.web.html._
import ac.frontend.utils.StreamOps
import ac.frontend.utils.spinners.ScaleLoader

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
          div(className := "notice-box")(
            h1(if (as == Victory) "You win!" else "You've lost"),
            rs match {
              case RematchState.NotAsked =>
                button(onClick := runRematch, className := "button")("Rematch")
              case RematchState.Asked =>
                button(onClick := runRematch, className := "button")("Agree to rematch")
              case _ =>
                ScaleLoader(10, 3, "2px", 3)
            }
          )
        )
      case _ => None
    }
  }
}
