package ac.frontend.pages

import slinky.core.StatelessComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html.div

import com.olegpy.shironeko.internals.SlinkyHotLoadingWorkaround._


@react class GameScreen extends StatelessComponent {
  type Props = Unit
  def render(): ReactElement = div()
}
