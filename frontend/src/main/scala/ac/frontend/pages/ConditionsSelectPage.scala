package ac.frontend.pages

import ac.frontend.Store
import ac.frontend.actions.{connect, settings}
import ac.frontend.components.PlayerDisplay
import ac.frontend.states.AppState.User
import ac.frontend.states.{PersistentSettings, GameConditionOptions}
import eu.timepit.refined.api.Refined
import monix.eval.Coeval
import cats.syntax.apply._
import org.scalajs.dom.raw.HTMLSelectElement
import slinky.core.{AttrPair, Component, StatelessComponent}
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html._

@react class MatchmakingPage extends StatelessComponent {
  case class Props(me: User, enemy: User, children: ReactElement*)

  def render(): ReactElement = {
    div(className := "box wide")(
      PlayerDisplay(props.me),
      props.children,
      PlayerDisplay(props.enemy)
    )
  }
}

@react class ConditionsSelectPage extends Component {
  type Props = Unit
  case class State(locationName: String, cards: Int, buttonDisabled: Boolean = false)

  def initialState: State = {
    val s = PersistentSettings[Coeval].readAll.value()
    State(s.tavern, s.cards)
  }

  def render(): ReactElement = {
    val conds = GameConditionOptions.taverns(state.locationName)
      .copy(handSize = Refined.unsafeApply(state.cards))

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
        GameConditionOptions.taverns.map { case (tavernName, _) =>
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
          disabled := state.buttonDisabled,
          onClick := {() =>
            this.setState(_.copy(buttonDisabled = true))
            Store.execS { implicit alg =>
            settings.persistConditions(state.locationName, state.cards) *>
            connect.supplyConditions(conds)
          }}
        )(s"Confirm")
      ),
    )
  }
}
