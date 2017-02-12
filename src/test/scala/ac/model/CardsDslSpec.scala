package ac.model

import ac.UnitSpec
import CardsDsl._

import scala.language.postfixOps

class CardsDslSpec extends UnitSpec {
  val testState = State(
    Player(Buildings(25, 17), Resources(3, 4, 5), Resources(2, 2, 2)),
    Player(Buildings(30, 2), Resources(10, 6, 15), Resources(1, 1, 1)),
    Vector()
  )

  "Special operator `swap walls`" should "swap player walls" in {
    val op = swap walls

    val swapped = op(testState)

    swapped.stats.buildings.wall shouldEqual testState.enemy.buildings.wall
    swapped.enemy.buildings.wall shouldEqual testState.stats.buildings.wall

    op(swapped) shouldEqual testState
  }

  "Additive syntax words" should "correctly update relevant stat" in {
    val testData: Seq[(StatWord, Player => Int)] = Seq(
      (brick, _.resources.bricks),
      (bricks, _.resources.bricks),
      (gem, _.resources.gems),
      (gems, _.resources.gems),
      (recruit, _.resources.recruits),
      (recruits, _.resources.recruits),
      (quarry, _.income.bricks),
      (quarries, _.income.bricks),
      (magic, _.income.gems),
      (dungeon, _.income.recruits),
      (dungeons, _.income.recruits),
      (wall, _.buildings.wall),
      (tower, _.buildings.tower)
    )

    for {
      (stat, read) <- testData
      i <- -3 to 3
    } {
      val pfn = +i player stat
      val efn = +i enemy stat
      val afn = +i all stat

      val Seq(pState, eState, aState) = Seq(pfn, efn, afn).map(_(testState))
      read(pState.stats) should equal (read(testState.stats) + i)
      read(eState.enemy) should equal (read(testState.enemy) + i)
      read(aState.stats) should equal (read(testState.stats) + i)
      read(aState.enemy) should equal (read(testState.enemy) + i)
    }
  }

  "Specifying `worth`" should "set card's costNumber" in {
    for {
      x <- Seq(0, 1, 8, 23)
      b = "TestCard" worth x
      card <- Seq(b bricks (), b gems (), b recruits ())
    } {
      card.costNumber shouldEqual x
    }
  }
}
