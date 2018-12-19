package ac.frontend.components

import ac.frontend.utils
import slinky.core.StatelessComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html.{className, div}


@react class DummyCards extends StatelessComponent {
  type Props = Unit

  def render(): ReactElement =
    div(className := "dummy-cards")(if (utils.isDevelopment) "<Dummy>" else " ")
}
