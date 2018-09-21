package ac.frontend


import scala.scalajs.js
import scala.scalajs.js.Dynamic.global

import ac.syntax.delay
import cats.data.{Nested}
import cats.effect.{Concurrent, Sync}
import fs2.Stream
import org.scalajs.dom.raw.Location
import org.scalajs.dom.window
import cats.implicits._

object utils {
  def parseQueryString(str: String): Map[String, String] = {
    val norm = if (str startsWith "?") str.drop(1) else str
    norm
      .split("&")
      .filterNot(s => s.isEmpty || s == "=")
      .map { string =>
        val arr = string.split("=")
          .map(global.decodeURIComponent.asInstanceOf[js.Function1[String, String]])
          .take(2)
        (arr(0), arr.lift(1).getOrElse(""))
      }
      .toMap
  }

  def currentUrl[F[_]: Sync]: F[Location] =
    delay[F].of(window.location)

  /*_*/
  def zipND[F[_]: Concurrent, A, B](fa: Stream[F, A], fb: Stream[F, B]): Stream[F, (A, B)] =
    Nested(fa.holdOption).product(Nested(fb.holdOption)).value.flatMap(_.discrete).collect {
      case (Some(a), Some(b)) => (a, b)
    }
  /*_*/
}
