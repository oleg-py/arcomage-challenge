package ac

package object communication {
  type EitherId[Req, Res] = (Int, Req) Either (Int, Res)
}
