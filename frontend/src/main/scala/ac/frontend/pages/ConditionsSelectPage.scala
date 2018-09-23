package ac.frontend.pages

import ac.frontend.Store
import ac.frontend.actions.connect
import ac.frontend.components.PlayerDisplay
import ac.frontend.states.AppState.SupplyingConditions
import ac.game.GameConditions
import com.olegpy.shironeko.internals.SlinkyHotLoadingWorkaround._
import slinky.core.Component
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html._

@react class ConditionsSelectPage extends Component {
  type Props = SupplyingConditions
  type State = GameConditions

  def initialState: State = GameConditions.testing

  def render(): ReactElement = {
    div(className := "box wide")(
      PlayerDisplay(props.me),
      div(
        div("Only standard conditions are currently supported"),
        div(s"Cards: ${state.handSize}"),
        div(s"Tower to win: ${state.victoryConditions.tower}"),
        div(s"Resources to win: ${state.victoryConditions.resources}"),
        hr(),
        div(s"Tower: ${state.initialStats.buildings.tower.value}"),
        div(s"Wall: ${state.initialStats.buildings.wall.value}"),
        div(s"Income: ${state.initialStats.income.bricks.value}"),
        div(s"Resources: ${state.initialStats.resources.bricks.value}"),
        div(className := "button-container-right")(
          button(
            className := "button",
            onClick := {() => Store.execS { implicit alg =>
              connect.supplyConditions(state)
            }}
          )(s"Confirm")
        ),
      ),
      PlayerDisplay(props.other)

    )
  }
}
