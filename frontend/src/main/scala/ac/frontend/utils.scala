package ac.frontend


import scala.scalajs.js
import scalajs.js.Dynamic.global

import org.scalajs.dom.window
import cats.effect.Sync
import ac.syntax.delay
import org.scalajs.dom.raw.Location


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
}
