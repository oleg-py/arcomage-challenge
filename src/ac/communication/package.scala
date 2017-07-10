package ac

package object communication {
  type EitherID[Req, Res] = (Int, Req) Either (Int, Res)
}
