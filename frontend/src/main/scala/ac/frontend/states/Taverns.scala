package ac.frontend.states

import scala.collection.immutable.ListMap
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

import ac.game.{GameConditions, VictoryConditions}
import ac.game.player.{Buildings, Player}
import cats.data.Validated.{Invalid, Valid}
import cats.data.ValidatedNel
import cats.implicits._
import eu.timepit.refined.types.numeric.{NonNegInt, PosInt}
import eu.timepit.refined.cats.syntax._


object Taverns {
  def apply(): ListMap[String, GameConditions] = map

  private val map = {
    val std = GameConditions.testing
    val (valids, errors) = RawData.map(validate).partition(_.isValid)

    errors
      .collect { case Invalid(nel) => nel }
      .foreach { nel =>
        Console.err.println(s"Errorneous tavern data: ${nel.mkString_("[", ", ", "]")}")
      }
    val entries = valids.collect {
      case Valid((name, tower, wall, win_tower, win_resource)) =>
        name -> GameConditions(
          std.handSize,
          Player(
            Buildings(tower, wall),
            std.initialStats.resources,
            std.initialStats.income
          ),
          VictoryConditions(win_tower, win_resource)
        )
    }
    ListMap(entries: _*)
  }

  private def validate(raw: Entry): ValidatedNel[String, (String, NonNegInt, NonNegInt, PosInt, PosInt)] =
    ( NonNegInt.validate(raw.tower)
    , NonNegInt.validate(raw.wall)
    , PosInt.validate(raw.win_tower)
    , PosInt.validate(raw.win_resource)).mapN((raw.name, _, _, _, _))

  private trait Entry extends js.Object {
    def name: String
    def tower: Int
    def wall: Int
    def win_tower: Int
    def win_resource: Int
  }
  @JSImport("resources/taverns.csv", JSImport.Namespace)
  @js.native
  private object RawData extends js.Array[Entry]
}
