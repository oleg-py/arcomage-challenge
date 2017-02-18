package ac.model

case class Resources (
  bricks:   Int = 0,
  gems:     Int = 0,
  recruits: Int = 0
) {
  def asSeq: Seq[Int] = Seq(bricks, gems, recruits)
}
