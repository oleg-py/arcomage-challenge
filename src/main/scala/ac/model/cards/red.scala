package ac.model.cards

import ac.model.Card
import ac.model.CardsDsl._

import scala.language.postfixOps

object red {
  //noinspection ScalaUnnecessaryParentheses
  val cards: Vector[Card] = Vector(
    "Brick Shortage" worth 0 bricks (
      -8 all bricks
    ),

    "Earthquake" worth 0 bricks (
      -1 all quarrys
    ),

    "Lucky Cache" worth 0 bricks (
      +2 player bricks,
      +2 player gems,
      play again
    ),

    "Strip Mine" worth 0 bricks (
      -1 player quarry,
      +10 player wall,
      +5 player gems
    ),

    "Friendly Terrain" worth 1 brick (
      +1 player wall,
      play again
    ),

    "Rock Garden" worth 1 brick (
      +1 player wall,
      +1 player tower,
      +2 player recruits
    ),

    "Basic Wall" worth 2 bricks (
      +3 player wall
    ),

    "Innovations" worth 2 bricks (
      +1 all quarrys,
      +4 player gems
    ),

    "Work Overtime" worth 2 bricks (
      +5 player wall,
      -6 player gems
    ),

    "Foundations" worth 3 bricks (
      when (wall is 0) {
        +6 player wall
      } otherwise {
        +3 player wall
      }
    ),

    "Miners" worth 3 bricks (
      +1 player quarry
    ),

    "Sturdy Wall" worth 3 bricks (
      +4 player wall
    ),

    "Collapse!" worth 4 bricks (
      -1 enemy quarry
    ),

    "Mother Lode" worth 4 bricks (
      when (quarry < enemy quarry) {
        +2 player quarry
      } otherwise {
        +1 player quarry
      }
    ),

    "Big Wall" worth 5 bricks (
      +6 player wall
    ),

    "Copping the Tech" worth 5 bricks (
      when (quarry < enemy quarry) {
        quarry := enemy.quarry
      }
    ),

    "Flood Water" worth 6 bricks (
      when (wall < enemy wall) (
        -1 player dungeon,
        -2 player tower
      ) otherwise (
        -1 enemy dungeon,
        -2 enemy tower
      )
    ),

    "New Equipment" worth 6 bricks (
      +2 player quarry
    ),

    "Dwarven Miners" worth 7 bricks (
      +4 player wall,
      +1 player quarry
    ),

    "Forced Labor" worth 7 bricks (
      +9 player wall,
      -5 player recruits
    ),

    "Tremors" worth 7 bricks (
      -5 all walls,
      play again
    ),

    "Reinforced Wall" worth 8 bricks (
      +8 player wall
    ),

    "Secret Room" worth 8 bricks (
      +1 player magic,
      play again
    ),

    "Crystal Rocks" worth 9 bricks (
      +7 player wall,
      +7 player gems
    ),

    "Porticulus" worth 9 bricks (
      +5 player wall,
      +1 player dungeon
    ),

    "Barracks" worth 10 bricks (
      +6 player recruits,
      +6 player wall,
      when (dungeon < enemy dungeon) {
        +1 player dungeon
      }
    ),

    "Harmonic Ore" worth 11 bricks (
      +6 player wall,
      +3 player tower
    ),

    "Mondo Wall" worth 13 bricks (
      +12 player wall
    ),

    "Battlements" worth 14 bricks (
      +7 player wall,
      6 damage dealt
    ),

    "Focused Designs" worth 15 bricks (
      +8 player wall,
      +5 player tower
    ),

    "Great Wall" worth 16 bricks (
      +15 player wall
    ),

    "Shift" worth 17 bricks (
      swap walls
    ),

    "Rock Launcher" worth 18 bricks (
      +6 player wall,
      10 damage dealt
    ),

    "Dragon's Heart" worth 24 bricks (
      +20 player wall,
      +8 player tower
    )
  )
}
