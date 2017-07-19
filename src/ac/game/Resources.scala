package ac.game

case class Resources (
  bricks:   Int = 0,
  gems:     Int = 0,
  recruits: Int = 0
) {
  def asSeq: Seq[Int] = Seq(bricks, gems, recruits)

  def + (r: Resources) = Resources(
    bricks   + r.bricks,
    gems     + r.gems,
    recruits + r.recruits
  )

  def - (r: Resources) = this + (-r)
  def unary_-() = this * -1

  def *   (i: Int) = Resources(bricks * i, gems * i, recruits * i)
  def max (i: Int) = Resources(bricks max i, gems max i, recruits max i)

  def all_<= (r: Resources) = this.asSeq zip r.asSeq forall { case (a, b) => a <= b }
}

object Resources {
  def all(x: Int) = Resources(x, x, x)
}
