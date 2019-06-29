package ac.frontend.states

import scala.collection.immutable.ListMap
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

import ac.frontend.i18n.Tr
import ac.game.{GameConditions, Resources, VictoryConditions}
import ac.game.player.{Buildings, Player}
import cats.data.Validated.{Invalid, Valid}
import cats.data.ValidatedNel
import cats.implicits._
import eu.timepit.refined.types.numeric.{NonNegInt, PosInt}
import eu.timepit.refined.cats.syntax._
import eu.timepit.refined.auto._


object GameConditionOptions {

  object presets {
    val tutorial = GameConditions(6, Player(
      Buildings(20, 10),
      Resources.all(12),
      Resources.all(2),
    ), VictoryConditions(60, 100))

    val fastGame = GameConditions(7, Player(
      Buildings(25, 15),
      Resources.all(16),
      Resources.all(3),
    ), VictoryConditions(120, 250))

    val hardcore = GameConditions(6, Player(
      Buildings(30, 50),
      Resources.all(20),
      Resources.all(1)
    ), VictoryConditions(150, 500))
  }

  def taverns: ListMap[String, (Tr[String], GameConditions)] = {
    val std = GameConditions.testing
    val (valids, errors) = RawData.map(validate).partition(_.isValid)

    errors
      .collect { case Invalid(nel) => nel }
      .foreach { nel =>
        Console.err.println(s"Errorneous tavern data: ${nel.mkString_("[", ", ", "]")}")
      }
    val entries = valids.collect {
      case Valid((name, tower, wall, win_tower, win_resource)) =>
        name.en -> (name ->
          GameConditions(
            std.handSize,
            Player(
              Buildings(tower, wall),
              std.initialStats.resources,
              std.initialStats.income
            ),
            VictoryConditions(win_tower, win_resource)
          )
        )
    }
    ListMap(entries: _*)
  }

  private def validate(raw: Entry): ValidatedNel[String, (Tr[String], NonNegInt, NonNegInt, PosInt, PosInt)] =
    ( NonNegInt.validate(raw.tower)
    , NonNegInt.validate(raw.wall)
    , PosInt.validate(raw.win_tower)
    , PosInt.validate(raw.win_resource)).mapN((Tr(raw.name, raw.name_ru), _, _, _, _))

  private trait Entry extends js.Object {
    def name: String
    def name_ru: String
    def tower: Int
    def wall: Int
    def win_tower: Int
    def win_resource: Int
  }

  @JSImport("resources/taverns.csv", JSImport.Namespace)
  @js.native
  private object RawData extends js.Array[Entry]
}
