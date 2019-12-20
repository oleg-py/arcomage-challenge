package ac.frontend.components


import scala.scalajs.js
import scala.scalajs.js.|

import ac.game.{GameConditions, Resources}
import slinky.core.{StatelessComponent, TagMod}
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html.div
import ac.frontend.facades.AntDesign._
import ac.frontend.i18n.{Tr, withLang}
import eu.timepit.refined.api.Refined
import eu.timepit.refined.types.numeric.{NonNegInt, PosInt}
import org.scalablytyped.runtime.NumberDictionary
import typings.antd.libInputDashNumberMod.InputNumberProps
import typings.antd.libRadioInterfaceMod.RadioGroupProps
import typings.antd.libSliderMod.{SliderProps, SliderValue}
import typings.antd.{antdStrings => $}
import monocle.macros.syntax.lens._

@react class CustomConditionsForm extends StatelessComponent {
  case class Props(initial: GameConditions, onChange: GameConditions => Unit)

  private[this] def mutate(f: GameConditions => GameConditions) = props.onChange(f(props.initial))

  private[this] def syncRange(label: ReactElement)(
    min: Int,
    max: Int,
    lo: Int,
    hi: Int,
    marks: NumberDictionary[String],
    onChange: (Int, Int) => Unit
  ) = {
    def range(from: Int, to: Int): SliderValue = |.from(js.Tuple2(from.toDouble, to.toDouble))

    Row.of(
      Col.span(3)(label),
      Col.span(3)(InputNumber(InputNumberProps(
        min = 1,
        max = max min hi,
        value = lo,
        onChange = _.map(_.toInt).foreach(onChange(_, hi))
      ))),
      Col.span(15)(Slider(SliderProps(
        min = min,
        max = max,
        range = true,
        marks = marks,
        value = range(lo, hi),
        onChange = x => {
          val js.Tuple2(lo, hi) = x.asInstanceOf[js.Tuple2[Double, Double]]
          onChange(lo.toInt, hi.toInt)
        }
      ))),
      Col.span(3)(
        InputNumber(InputNumberProps(
          min = 1 max lo,
          max = max,
          value = hi,
          onChange = _.map(_.toInt).foreach(onChange(lo, _))
        ))
      ),
    )
  }

  private[this] def slider(label: ReactElement)(
    min: Int,
    max: Int,
    value: Int,
    marks: NumberDictionary[String],
    onChange: Int => Unit
  ) = {
      Row.of(
        Col.span(3)(label),
        Col.span(18)(Slider(SliderProps(
          min = min,
          max = max,
          value = value,
          marks = marks,
          onChange = sv => onChange(sv.asInstanceOf[Int])
        ))),
        Col.span(3)(InputNumber(InputNumberProps(
          min = min,
          max = max,
          value = value,
          onChange = i => i.map(_.toInt).foreach(onChange)
        )))
      )
    }

  override def render(): ReactElement = withLang { implicit lang =>
    val c = props.initial
    def marks(is: Int*): NumberDictionary[String] = {
      js.Dictionary(is.map(i => i.toString -> i.toString): _*).asInstanceOf[NumberDictionary[String]]
    }

    val towerStart = c.initialStats.buildings.tower.value
    val towerEnd = c.victoryConditions.tower.value

    val rsStart = c.initialStats.resources.bricks.value
    val rsEnd = c.victoryConditions.resources.value

    div(
      syncRange(Tr("Tower", "Башня"))(
        min = 1,
        max = 300,
        lo = towerStart,
        hi = towerEnd,
        marks = marks(50 to 250 by 50: _*),
        onChange = (lo, hi) => mutate {
          _
            .lens(_.initialStats.buildings.tower).set(NonNegInt.unsafeFrom(lo))
            .lens(_.victoryConditions.tower).set(PosInt.unsafeFrom(hi))
        }
      ),
      slider(Tr("Wall", "Стена"))(
        min = 0,
        max = 300,
        value = c.initialStats.buildings.wall.value,
        marks = marks(50 to 250 by 50: _*),
        onChange = i => mutate(_.lens(_.initialStats.buildings.wall).set(Refined.unsafeApply(i)))
      ),
      syncRange(Tr("Res.", "Рес."))(
        min = 0,
        max = 500,
        lo = rsStart,
        hi = rsEnd,
        marks = marks(100 to 400 by 100: _*),
        onChange = (lo, hi) => mutate {
          _
            .lens(_.initialStats.resources).set(Resources.all(NonNegInt.unsafeFrom(lo)))
            .lens(_.victoryConditions.resources).set(Refined.unsafeApply(hi))
        }
      ),
      slider(Tr("Income", "Доход"))(
        min = 1,
        max = 9,
        value = c.initialStats.income.bricks.value,
        marks = marks(3, 6),
        onChange = i => mutate(_.lens(_.initialStats.income).set(Resources.all(PosInt.unsafeFrom(i))))
      ),
      Row.of(
        Col.span(24)(
          Radio.Group(RadioGroupProps(
            value = c.handSize.value,
            buttonStyle = $.solid,
            onChange = { e =>
              val handSize = e.target.value.asInstanceOf[Int]
              mutate(GameConditions.handSize.set(Refined.unsafeApply(handSize)))
            }
          ))(
            (4 to 7).map { i =>
              Radio.Button.of(i)(Tr(s"$i cards", s"$i карт(ы)")): TagMod[Nothing]
            }: _*
          )
        )
      )
    )
  }
}
