package ac.frontend.pages

import ac.frontend.i18n._
import ac.frontend.components.CustomConditionsForm
import ac.frontend.facades.AntDesign.{Button, Option, Select, TabPane, Tabs, Text}
import ac.frontend.states.ConditionsChoice.{FastGame, FullyCustom, Hardcore, Mode, Preset, PresetMode, Tavern, Tutorial}
import ac.frontend.states.{ConditionsChoice, GameConditionOptions, PersistentSettings}
import ac.game.VictoryConditions
import ac.game.player.Buildings
import eu.timepit.refined.types.numeric.{NonNegInt, PosInt}
import monix.eval.Coeval
import slinky.core.Component
import slinky.core.annotations.react
import slinky.core.facade.{Fragment, ReactElement}
import slinky.web.html._
import monocle.macros.syntax.lens._
import typings.antd.antdComponents.{SelectProps, TabPaneProps, TabsProps}

/*_*/
@react class ConditionsSelectPage extends Component {
  type Props = ConditionsChoice => Unit
  case class State(cc: ConditionsChoice, buttonDisabled: Boolean = false)

  private def tavern = GameConditionOptions.taverns(state.cc.tavern)._2

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

  def render(): ReactElement = withLang { implicit lang =>
    div(
      Tabs(TabsProps(
        activeKey = state.cc.mode.key,
        onChange = { key => setState(_.lens(_.cc.mode).set(Mode.ofKey(key)))}
      ))(
        TabPane(TabPaneProps(tab = Tr("Quick Presets", "Быстрые опции") in lang)).withKey(PresetMode.key)(
          div(className := "quick-presets-select")(
            label(
              presetInput(FastGame),
              dl(
                dt(Tr("Fast game", "Быстрая игра")),
                dd(Tr(
                  en = "Increased resource income and maximum cards for quick start",
                  ru = "Увеличенный доход и макс. количество карт на руках"
                )),
              )
            ),
            label(
              presetInput(Tutorial),
              dl(
                dt(Tr("Tutorial", "Обучение")),
                dd(Tr(
                  en = "Easy and short game mode for teaching newcomers",
                  ru = "Легкий и короткий режим для обучения новичков"
                ))
              )
            ),
            label(
              presetInput(Hardcore),
              dl(
                dt(Tr("Hardcore", "Хардкор")),
                dd(Tr(
                  en = "Slow-paced game, perfect for tie breakers",
                  ru = "Медленная игра, подходит для решающих матчей"
                ))
              )
            ),
          )
        ),
        TabPane(TabPaneProps(tab = Tr("MM7 Presets", "Опции MM7") in lang)).withKey(Tavern.key)(
          Select(SelectProps[String](
            value = state.cc.tavern,
            onChange = { (value: String, _) =>
              setState(_.lens(_.cc.tavern).set(value))
            },
            className = "tavern-select"
          ))(
            GameConditionOptions.taverns.map { case (tavernKey, (name, _)) =>
              Option(tavernKey, name in lang)
            }
          ),
          div(className := "tavern-description") {
            val resources = Tr[(NonNegInt, PosInt) => String](
              (r, i) => s"Resources at start: $r, income: $i",
              (r, i) => s"Начальные ресурсы: $r, доход: $i"
            )
            val startBuildings = Tr[Buildings => String](
              b => s"Starting tower: ${b.tower}, wall: ${b.wall}",
              b => s"Начальная башня: ${b.tower}, стена: ${b.wall}"
            )
            val victory = Tr[VictoryConditions => String](
              vc => s"Tower to win: ${vc.tower}, resources to win: ${vc.resources}",
              vc => s"Башня для победы: ${vc.tower}, ресурсы: ${vc.resources}"
            )
            val cards = Tr[Int => String](
              c => s"Cards: $c",
              c => s"Карты: $c"
            )

            Fragment(
              Text(resources.in(lang)(
                tavern.initialStats.resources.bricks,
                tavern.initialStats.income.bricks
              )),
              Text(startBuildings.in(lang)(tavern.initialStats.buildings)),
              Text(victory.in(lang)(tavern.victoryConditions)),
              Text(cards.in(lang)(tavern.handSize.value))
            )
          },
        ),
        TabPane(TabPaneProps(tab = Tr("Custom", "Свои опции") in lang)).withKey(FullyCustom.key)(
          div(className := "custom-conditions")(
            CustomConditionsForm(
              state.cc.customPattern,
              gc => setState(_.lens(_.cc.customPattern).set(gc))
            )
          )
        ),
      ),
      div(className := "button-container-right")(
        Button(
          disabled = state.buttonDisabled || state.cc.pick().isEmpty,
          onClick = () => {
            setState(_.copy(buttonDisabled = true))
            props(state.cc)
          })(Tr("Confirm", "Далее"))
      ),
    )
  }
}
