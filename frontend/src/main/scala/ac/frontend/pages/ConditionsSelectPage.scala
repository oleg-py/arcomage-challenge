package ac.frontend.pages

import ac.frontend.Store
import ac.frontend.actions.connect
import ac.frontend.components.PlayerDisplay
import ac.frontend.states.AppState.User
import ac.frontend.states.Taverns
import eu.timepit.refined.api.Refined
import org.scalajs.dom.raw.HTMLSelectElement
import slinky.core.{AttrPair, Component}
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html._

@react class ConditionsSelectPage extends Component {
  case class Props(me: User, enemy: User)
  case class State(locationName: String, cards: Int)

  def initialState: State = State(Taverns().head._1, 6)

  def render(): ReactElement = {
    val conds = Taverns().apply(state.locationName)
      .copy(handSize = Refined.unsafeApply(state.cards))

    div(className := "box wide")(
      PlayerDisplay(props.me),
      div(
        div(
          s"Cards: ",
          select(
            (value := state.cards.toString).asInstanceOf[AttrPair[select.tag.type]],
            onChange := { e =>
              val value = e.target.asInstanceOf[HTMLSelectElement].value.toInt
              setState(_.copy(cards = value))
            }
          )(
            option(value := "5")("5"),
            option(value := "6")("6"),
            option(value := "7")("7")
          )
        ),

        select(
          (value := state.locationName).asInstanceOf[AttrPair[select.tag.type]],
          onChange := { e =>
            val value = e.target.asInstanceOf[HTMLSelectElement].value
            setState(_.copy(locationName = value))
          }
        )(
          Taverns().map { case (tavernName, _) =>
            option(
              key := tavernName,
              value := tavernName
            )(tavernName)
          }
        ),
        div(s"Tower to win: ${conds.victoryConditions.tower}"),
        div(s"Resources to win: ${conds.victoryConditions.resources}"),
        hr(),
        div(s"Tower: ${conds.initialStats.buildings.tower.value}"),
        div(s"Wall: ${conds.initialStats.buildings.wall.value}"),
        div(s"Income: ${conds.initialStats.income.bricks.value}"),
        div(s"Resources: ${conds.initialStats.resources.bricks.value}"),
        div(className := "button-container-right")(
          button(
            className := "button",
            onClick := {() => Store.execS { implicit alg =>
              connect.supplyConditions(conds)
            }}
          )(s"Confirm")
        ),
      ),
      PlayerDisplay(props.enemy)

    )
  }
}
