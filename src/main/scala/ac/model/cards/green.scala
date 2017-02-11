package ac.model.cards

import ac.model.Card
import ac.model.CardsDsl._

import scala.language.postfixOps

object green {
  val cards: Vector[Card] = Vector(
    "Full Moon" worth 0 recruits (
      +1 all dungeons,
      +3 player recruits
    ),

    "Mad Cow Disease" worth 0 recruits (
      -6 all recruits
    ),

    "Faerie" worth 1 recruit (
      2 damage dealt,
      play again
    ),

    "Moody Goblins" worth 1 recruit (
      4 damage dealt,
      -3 player gems
    ),

    "Elven Scout" worth 2 recruits (
      discard card,
      play again
    ),

    "Gnome" worth 2 recruits (
      3 damage dealt,
      +1 player gem
    ),

    "Spearman" worth 2 recruits (
      when (wall > enemy wall) {
        3 damage dealt
      } otherwise {
        2 damage dealt
      }
    ),

    "Goblin Mob" worth 3 recruits (
      6 damage dealt,
      3 damage received
    ),

    "Minotaur" worth 3 recruits (
      +1 player dungeon
    ),

    "Orc" worth 3 recruits (
      5 damage dealt
    ),

    "Berserker" worth 4 recruits (
      8 damage dealt,
      -3 player tower
    ),

    "Goblin Archers" worth 4 recruits (
      -3 player tower,
      1 damage received
    ),

    "Dwarves" worth 5 recruits (
      4 damage dealt,
      +3 player wall
    ),

    "Imp" worth 5 recruits (
      6 damage dealt,
      -5 all bricks,
      -5 all gems,
      -5 all recruits
    ),

    "Slasher" worth 5 recruits (
      6 damage dealt
    ),

    "Little Snakes" worth 6 recruits (
      -4 enemy tower
    ),

    "Ogre" worth 6 recruits (
      7 damage dealt
    ),

    "Rabid Sheep" worth 6 recruits (
      6 damage dealt,
      -3 enemy recruits
    ),

    "Shadow Faerie" worth 6 recruits (
      -2 enemy tower,
      play again
    ),

    "Troll Trainer" worth 7 recruits (
      +2 player dungeons
    ),

    "Spizzer" worth 8 recruits (
      when (enemy.wall is 0) {
        10 damage dealt
      } otherwise {
        6 damage dealt
      }
    ),

    "Tower Gremlin" worth 8 recruits (
      2 damage dealt,
      +4 player tower,
      +2 player wall
    ),

    "Unicorn" worth 9 recruits (
      when (magic > enemy magic) {
        12 damage dealt
      } otherwise {
        8 damage dealt
      }
    ),

    "Werewolf" worth 9 recruits (
      9 damage dealt
    ),

    "Elven Archers" worth 10 recruits (
      when (wall > enemy wall) {
        -6 enemy tower
      } otherwise {
        6 damage dealt
      }
    ),

    "Corrosion Cloud" worth 11 recruits (
      when (enemy.wall > 0) {
        10 damage dealt
      } otherwise {
        7 damage dealt
      }
    ),

    "Rock Stompers" worth 11 recruits (
      8 damage dealt,
      -1 enemy quarry
    ),

    "Thief" worth 12 recruits (
      -10 enemy gems,
      -5 enemy bricks,
      +5 player gems,
      +3 player bricks
    ),

    "Warlord" worth 13 recruits (
      13 damage dealt,
      -3 player gems
    ),

    "Succubus" worth 14 recruits (
      -5 enemy tower,
      -8 enemy recruits
    ),

    "Stone Giant" worth 15 recruits (
      10 damage dealt,
      +4 player wall
    ),

    "Vampire" worth 17 recruits (
      10 damage dealt,
      -5 enemy recruits,
      -1 enemy dungeon
    ),

    "Pegasus Lancer" worth 18 recruits (
      -12 enemy tower
    ),

    "Dragon" worth 25 recruits (
      20 damage dealt,
      -10 enemy gems,
      -1 enemy dungeons
    )
  )
}
