package ac.frontend.components

import scala.scalajs.js

import ac.game.GameConditions
import slinky.core.{Component, FunctionalComponent, StatelessComponent}
import slinky.core.annotations.react
import slinky.core.facade.{React, ReactElement}
import slinky.web.html.div
import typings.antdLib.libFormFormMod.WrappedFormUtils
import typings.antdLib.libFormMod.default.create

@react object CustomConditionsForm {
  case class Props(initial: GameConditions, onSubmit: GameConditions => Unit)

  private[this] val form =
    create().apply(js.constructorOf[Underlying]).asInstanceOf[ReactElement]

  val component = FunctionalComponent[Unit] { _ =>
    React.createElement(form, null)
  }
}
@react private class Underlying extends StatelessComponent {
  case class Props(
    form: WrappedFormUtils[_]
  )

  def render(): ReactElement = {
    div(props.form.toString)
  }
}