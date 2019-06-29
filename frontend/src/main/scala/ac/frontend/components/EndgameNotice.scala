package ac.frontend.components

import ac.frontend.Store
import ac.frontend.actions.matches
import ac.frontend.facades.AntDesign.{Button, Spin}
import ac.frontend.states.{AppState, RematchState, StoreAlg}
import ac.frontend.states.AppState.{Defeat, Draw, Victory}
import slinky.core.facade.ReactElement
import slinky.web.html._
import com.olegpy.shironeko.util.combine
import typings.antdLib.antdLibComponents.SpinProps

object EndgameNotice extends Store.ContainerNoProps {
  case class State(app: AppState, rs: RematchState)

  def subscribe[F[_]: Subscribe]: fs2.Stream[F, State] =
    combine[State].from(
      StoreAlg[F].app.discrete,
      StoreAlg[F].rematchState.discrete
    )

  def render[F[_]: Render](state: State): ReactElement = {
    val State(as, rs) = state

    val runRematch = () => exec { matches.proposeRematch[F] }

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
