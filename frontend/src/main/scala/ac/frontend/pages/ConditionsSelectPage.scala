package ac.frontend.pages

import ac.frontend.Store
import ac.frontend.actions.{connect, settings}
import ac.frontend.facades.tabs._
import ac.frontend.states.ConditionsChoice.{FastGame, Hardcore, Preset, PresetMode, Tavern, Tutorial}
import ac.frontend.states.{ConditionsChoice, GameConditionOptions, PersistentSettings}
import monix.eval.Coeval
import cats.implicits._
import org.scalajs.dom.raw.HTMLSelectElement
import slinky.core.Component
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html._
import monocle.macros.syntax.lens._

/*_*/
@react class ConditionsSelectPage extends Component {
  type Props = Unit
  case class State(cc: ConditionsChoice, buttonDisabled: Boolean = false)

  def initialState: State = {
    State(PersistentSettings[Coeval].readAll.value().conditionsChoice)
  }

  private def presetInput(p: Preset) = {
    input(
      `type` := "radio",
      name := "quick-preset",
      value := p.toString,
      checked := state.cc.preset == p,
      onChange := { () => setState(_.lens(_.cc.preset).set(p)) }
    )
  }

  def render(): ReactElement = div(
    Tabs(
      activeKey = state.cc.mode match {
        case ConditionsChoice.PresetMode => "qp"
        case ConditionsChoice.Tavern => "mm7p"
      },
      renderTabBar = () => InkTabBar(
        onTabClick = { (key, _) =>
          setState(_.lens(_.cc.mode).set(key match {
            case "qp" => PresetMode
            case "mm7p" => Tavern
          }))
        }
      ),
      renderTabContent = () => TabContent(),
    )(
      TabPane(tab = "Quick Presets").withKey("qp")(
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
      TabPane(tab = "MM7 Presets").withKey("mm7p")(
        div(className := "taverns")(
          p("Select a city to play a game, classic style:"),
          select(
            className := "tavern-select",
            value := state.cc.tavern,
            onChange := { e =>
              val value = e.target.asInstanceOf[HTMLSelectElement].value
              setState(_.lens(_.cc.tavern).set(value))
            }
          )(
            GameConditionOptions.taverns.map { case (tavernName, _) =>
              option(
                key := tavernName,
                value := tavernName
              )(tavernName)
            }
          ),
        )
      ),
      TabPane(tab = "Custom").withKey("cp")(
        div(className := "custom-conditions")(
          "This section is in development"
        )
      ),
    ),
    div(className := "button-container-right")(
      button(
        className := "button",
        disabled := state.buttonDisabled || state.cc.pick().isEmpty,
        onClick := {() =>
          this.setState(_.copy(buttonDisabled = true))
          Store.execS { implicit alg =>
            settings.persistConditions(_ => state.cc) *>
            state.cc.pick().traverse_(connect.supplyConditions(_))
          }}
      )(s"Confirm")
    ),
  )
}
