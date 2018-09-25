package ac.frontend.states

import ac.game.VictoryConditions
import ac.game.player.CardScope
import monocle.macros.Lenses


@Lenses case class Progress(
  state: CardScope,
  conds: VictoryConditions
)

object Progress {
  val NotStarted = Progress(
    CardScope.Dummy,
    VictoryConditions.Dummy
  )
}
