package ac.frontend.components

import ac.frontend.Store
import ac.frontend.actions.matches
import ac.frontend.facades.AntDesign.{Spin, Button}
import ac.frontend.states.{AppState, RematchState}
import ac.frontend.states.AppState.{Defeat, Draw, Victory}
import slinky.core.facade.ReactElement
import slinky.web.html._
import ac.frontend.utils.StreamOps
import typings.antdLib.libSpinMod.SpinProps

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
      case Victory | Defeat | Draw =>
        div(className := "endgame-notice")(
          div(className := "notice-box")(
            h1(as match {
              case Victory => "You win!"
              case Defeat  => "You've lost"
              case _       => "It's a draw"
            }),
            rs match {
              case RematchState.NotAsked =>
                Button(onClick = runRematch)("Rematch")
              case RematchState.Asked =>
                Button(onClick = runRematch)("Agree to rematch")
              case _ =>
                Spin(SpinProps())
            }
          )
        )
      case _ => None
    }
  }
}
