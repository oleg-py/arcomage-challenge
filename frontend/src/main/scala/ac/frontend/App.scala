package ac.frontend

import ac.frontend.pages._
import ac.frontend.states.{AppState, StoreAlg}
import slinky.core.facade.ReactElement
import slinky.web.html._
import ac.frontend.actions.{connect, settings}
import ac.frontend.components.EndgameNotice
import ac.frontend.states.AppState._
import ac.frontend.utils.StreamOps
import ac.frontend.facades.AntDesign.Spin
import cats.effect.Concurrent
import com.olegpy.shironeko.util.combine
import cats.implicits._
import com.olegpy.shironeko.interop.Exec

object App extends Store.ContainerNoProps with Store.HasTimer {
  case class State(app: AppState, me: Option[User], enemy: Option[User])

  def subscribe[F[_]: Concurrent](implicit F: StoreAlg[F]): fs2.Stream[F, State] = {
    combine[State].from(
      F.app.discrete,
      F.me.discrete,
      F.enemy.discrete
    ).frameDebounced
  }

  def render[F[_]: Concurrent: StoreAlg: Exec](state: State): ReactElement = {
    div(className := "App")(
      state match {
        case State(_, None, _) =>
          // TODO - this causes random name to be persisted
          NameEntryPage { (name, email, avatar, hasName) => exec {
            settings.persistUser[F](name, email).whenA(hasName) *>
              connect[F](User(name, avatar))
          }}
        case State(AwaitingGuest(link), Some(me), _) =>
          AwaitingGuestPeerPage(me, link)
        case State(SupplyingConditions, Some(me), Some(enemy)) =>
          MatchmakingPage(me, enemy)(
            ConditionsSelectPage(cc => exec {
              settings.persistConditions[F](_ => cc) *>
              cc.pick().traverse_(connect.supplyConditions[F])
            }).withKey("csp")
          )
        case State(AwaitingConditions, Some(me), Some(enemy)) =>
          MatchmakingPage(me, enemy)(
            div(key := "waiting-notice", className := "conditions-waiting-notice")(
              Spin("Opponent is supplying conditions...")
            )
          )
        case State(Playing | Defeat | Victory | Draw, _, _) =>
          GameScreen()
        case _ =>
          div(className := "spinner-container")(
            Spin("Loading...")
          )
      },
      EndgameNotice()
    )
  }
}
