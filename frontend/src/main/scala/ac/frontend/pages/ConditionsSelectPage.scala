package ac.frontend.pages

import ac.frontend.facades.AntDesign.{Button, Option, Select, TabPane, Tabs, Text}
import ac.frontend.states.ConditionsChoice.{FastGame, Hardcore, Mode, Preset, PresetMode, Tavern, Tutorial}
import ac.frontend.states.{ConditionsChoice, GameConditionOptions, PersistentSettings}
import monix.eval.Coeval
import slinky.core.Component
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html._
import monocle.macros.syntax.lens._
import typings.antdLib.libSelectMod.SelectProps
import typings.antdLib.libTabsMod.{TabPaneProps, TabsProps}

/*_*/
@react class ConditionsSelectPage extends Component {
  type Props = ConditionsChoice => Unit
  case class State(cc: ConditionsChoice, buttonDisabled: Boolean = false)

  private def tavern = GameConditionOptions.taverns(state.cc.tavern)

  def initialState: State = {
    State(PersistentSettings[Coeval].readAll.value().conditionsChoice)
  }

  private def presetInput(p: Preset) = { // TODO port to ant-design?
    input(
      `type` := "radio",
      name := "quick-preset",
      value := p.toString,
      checked := state.cc.preset == p,
      onChange := { () => setState(_.lens(_.cc.preset).set(p)) }
    )
  }

  def render(): ReactElement = div(
    Tabs(TabsProps(
      activeKey = state.cc.mode.key,
      onChange = { key => setState(_.lens(_.cc.mode).set(Mode.ofKey(key)))}
    ))(
      TabPane(TabPaneProps(tab = "Quick Presets")).withKey(PresetMode.key)(
        div(className := "quick-presets-select")(
          label(
            presetInput(FastGame),
            dl(
              dt("Fast game"),
              dd("Increased resource income and maximum cards for quick start"),
            )
          ),
          label(
            presetInput(Tutorial),
            dl(
              dt("Tutorial"),
              dd("Easy and short game mode for teaching newcomers")
            )
          ),
          label(
            presetInput(Hardcore),
            dl(
              dt("Hardcore"),
              dd("Slow-paced game, perfect for tie breakers")
            )
          ),
        )
      ),
      TabPane(TabPaneProps(tab = "MM7 Presets")).withKey(Tavern.key)(
        Select(SelectProps[String](
          value = state.cc.tavern,
          onChange = { (value: String, _) =>
            setState(_.lens(_.cc.tavern).set(value))
          },
          className = "tavern-select"
        ))(
          GameConditionOptions.taverns.map { case (tavernName, _) =>
            Option(tavernName, tavernName)
          }
        ),
        div(className := "tavern-description")(
          Text(s"Resources at start: ${tavern.initialStats.resources.bricks}, " +
            s"income: ${tavern.initialStats.income.bricks}"),
          Text(s"Starting tower: ${tavern.initialStats.buildings.tower}, " +
            s"wall: ${tavern.initialStats.buildings.wall}"),
          Text(s"Tower to win: ${tavern.victoryConditions.tower}, " +
            s"resources to win: ${tavern.victoryConditions.resources}"),
          Text(s"Cards: ${tavern.handSize}"),
        ),
      ),
      TabPane(TabPaneProps(tab = "Custom", disabled = true)).withKey("cp")(
        div(className := "custom-conditions")(
          "This section is in development"
        )
      ),
    ),
    div(className := "button-container-right")(
      Button(
        disabled = state.buttonDisabled || state.cc.pick().isEmpty,
        onClick = () => {
          setState(_.copy(buttonDisabled = true))
          props(state.cc)
      })(s"Confirm")
    ),
  )
}
