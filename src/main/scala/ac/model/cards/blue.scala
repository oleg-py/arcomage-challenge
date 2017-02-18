package ac.model.cards

import CardsDsl._

import scala.language.postfixOps

object blue {
  val cards: Vector[Card] = Vector(
    "Bag of Baubles" worth 0 gems (
      when (tower < enemy tower) {
        +2 player tower
      } otherwise {
        +1 player tower
      }
    ),

    "Rainbow" worth 0 gems (
      +1 all towers,
      +3 player gems
    ),

    "Quartz" worth 1 gem (
      +1 player tower,
      play again
    ),

    "Amethyst" worth 2 gems (
      +3 player tower
    ),

    "Gemstone Flaw" worth 2 gems (
      -3 enemy tower
    ),

    "Prism" worth 2 gems (
      discard card,
      play again
    ),

    "Smoky Quartz" worth 2 gems (
      -1 enemy tower,
      play again
    ),

    "Power Burn" worth 3 gems (
      -5 player tower,
      +2 player magic
    ),

    "Ruby" worth 3 gems (
      +5 player tower
    ),

    "Spell Weavers" worth 3 gems (
      +1 player magic
    ),

    "Gem Spear" worth 4 gems (
      -5 enemy tower
    ),

    "Quarry's Help" worth 4 gems (
      +7 player tower,
      -10 player bricks
    ),

    "Solar Flare" worth 4 gems (
      +2 player tower,
      -2 enemy tower
    ),

    "Apprentice" worth 5 gems (
      +4 player tower,
      -3 player recruits,
      -2 enemy tower
    ),

    "Discord" worth 4 gems (
      -7 all towers,
      -1 all magic
    ),

    nondiscardable("Lodestone") worth 5 gems (
      +8 player tower
    ),

    "Crystal Matrix" worth 6 gems (
      +1 player magic,
      +3 player tower,
      +1 enemy tower
    ),

    "Emerald" worth 6 gems (
      +8 player tower
    ),

    "Crumblestone" worth 7 gems (
      +5 player tower,
      -6 enemy bricks
    ),

    "Harmonic Vibe" worth 7 gems (
      +1 player magic,
      +3 player tower,
      +3 player wall
    ),

    "Parity" worth 7 gems (
      when (magic < enemy magic) (
        magic := enemy.magic
      ) otherwise {
        enemy.magic := magic
      }
    ),

    "Crystallize" worth 8 gems (
      +11 player tower,
      -6 player wall
    ),

    "Shatterer" worth 8 gems (
      -1 player magic,
      -9 enemy tower
    ),

    "Pearl of Wisdom" worth 9 gems (
      +5 player tower,
      +1 player magic
    ),

    "Sapphire" worth 10 gems (
      +11 player tower
    ),

    "Lightning Shard" worth 11 gems (
      when (tower < enemy wall) {
        -8 enemy tower
      } otherwise {
        8 damage dealt
      }
    ),

    "Crystal Shield" worth 12 gems (
      +8 player tower,
      +3 player wall
    ),

    "Fire Ruby" worth 13 gems (
      +6 player tower,
      -4 enemy tower // TODO - description mentions "all towers"
    ),

    "Empathy Gem" worth 14 gems (
      +8 player tower,
      +1 player dungeon
    ),

    "Sanctuary" worth 15 gems (
      +10 player tower,
      +5 player wall,
      +5 player recruits
    ),

    "Diamond" worth 16 gems (
      +15 player tower
    ),

    "Lava Jewel" worth 17 gems (
      +12 player tower,
      6 damage dealt
    ),

    "Phase Jewel" worth 18 gems (
      +13 player tower,
      +6 player recruits,
      +6 player bricks
    ),

    "Dragon's Eye" worth 21 gems (
      +20 player tower
    )
  )
}
