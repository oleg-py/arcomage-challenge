package ac.game

case class Resources (
  bricks:   Int = 0,
  gems:     Int = 0,
  recruits: Int = 0
) {
  def asSeq: Seq[Int] = Seq(bricks, gems, recruits)

  def + (other: Resources) = Resources(
    bricks + other.bricks,
    gems + other.gems,
    recruits + other.recruits
  )
}
