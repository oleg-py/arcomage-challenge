package ac.frontend.components

import slinky.core.StatelessComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html.{className, div}


@react class DummyCards extends StatelessComponent {
  type Props = Unit

  def render(): ReactElement = div(className := "dummy-cards")("<Dummy>")
}
