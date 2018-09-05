package ac.game

import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.numeric.Greater
import player._
import shapeless.{Witness => W}

case class GameConditions (
  handSize: Int Refined Greater[W.`4`.T],
  initialStats: Player,
  victoryConditions: VictoryConditions
) {

  def initialState = CardScope(
    initialStats,
    initialStats,
    Vector()
  )
}

object GameConditions {
  def testing = GameConditions(
    6,
    Player(
      Buildings(25, 15),
      Resources.all(10),
      Resources.all(2)
    ),
    VictoryConditions(100, 200)
  )
}
