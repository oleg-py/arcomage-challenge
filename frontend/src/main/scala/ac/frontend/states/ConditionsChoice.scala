package ac.frontend.states

import ac.frontend.states.ConditionsChoice._
import ac.frontend.states.GameConditionOptions.{presets, taverns}
import ac.game.GameConditions
import upickle.default._

case class ConditionsChoice (
  mode: Mode = PresetMode,
  preset: Preset = FastGame,
  tavern: String = "Harmondale"
) {
  def pick(m: Mode = this.mode): Option[GameConditions] =
    m match {
      case PresetMode => Some {
        preset match {
          case FastGame => presets.fastGame
          case Tutorial => presets.tutorial
          case Hardcore => presets.hardcore
        }
      }
      case Tavern => taverns.get(tavern)
    }
}

object ConditionsChoice {
  implicit val pickler: ReadWriter[ConditionsChoice] = macroRW[ConditionsChoice]

  sealed trait Mode {
    def key: String = this match {
      case PresetMode => "quick-pattern"
      case Tavern => "mm7-pattern"
    }
  }
  case object PresetMode extends Mode
  case object Tavern extends Mode
  object Mode {
    def ofKey(k: String): Mode = List(PresetMode, Tavern)
      .find(_.key == k)
      .getOrElse(sys.error(s"Unexkected key: $k"))
    implicit val pickler: ReadWriter[Mode] = macroRW[Mode]
  }

  sealed trait Preset
  object Preset {
    implicit val pickler: ReadWriter[Preset] = macroRW[Preset]
  }
  case object FastGame extends Preset
  case object Tutorial extends Preset
  case object Hardcore extends Preset
}
