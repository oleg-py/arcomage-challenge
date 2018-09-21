package ac.frontend.pages

import ac.frontend.states.AppState.Playing
import ac.frontend.states.GameState.Progress
import slinky.core.StatelessComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html.div

import com.olegpy.shironeko.internals.SlinkyHotLoadingWorkaround._


@react class GameScreen extends StatelessComponent {
  case class Props(players: Playing, gameState: Progress)

  def render(): ReactElement = div()
}
