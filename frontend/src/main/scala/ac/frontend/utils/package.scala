package ac.frontend

import scala.scalajs.LinkingInfo

import cats.data.Nested
import cats.effect.Concurrent
import fs2.Stream
import cats.implicits._


package object utils {
  /*_*/
  implicit class StreamOps[F[_], A](private val self: Stream[F, A]) {
    def withLatestFrom[B](other: Stream[F, B])(implicit F: Concurrent[F]): Stream[F, (A, B)] =
      Nested(self.holdOption).product(Nested(other.holdOption)).value.flatMap(_.discrete).collect {
        case (Some(a), Some(b)) => (a, b)
      }
  }
  /*_*/

  def isDevelopment: Boolean = LinkingInfo.developmentMode
}
