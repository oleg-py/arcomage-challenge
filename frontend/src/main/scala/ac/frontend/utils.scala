package ac.frontend


import scala.scalajs.js
import scala.scalajs.js.Dynamic.global

import ac.game.cards.Card
import ac.game.player.Player
import ac.syntax.delay
import boopickle.Default._
import cats.Functor
import cats.effect.Sync
import eu.timepit.refined.api.Refined
import org.scalajs.dom.raw.Location
import org.scalajs.dom.window
import qq.droste.data.Fix

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

  object boopickleInstances {
    implicit val cardPickler: Pickler[Card] = generatePickler[Card]
    implicit val playerPickler: Pickler[Player] = generatePickler[Player]

    implicit def refinedType[T: Pickler, P]: Pickler[T Refined P] =
      transformPickler[T Refined P, T](Refined.unsafeApply)(Refined.unapply(_).get)

    implicit def pickleFix[F[_]: Functor](implicit p: Pickler[F[Unit]]): Pickler[Fix[F]] =
      new Pickler[Fix[F]] {
        override def pickle(f: Fix[F])(implicit state: PickleState): Unit = {

          val fields = new collection.mutable.ArrayBuffer[Fix[F]](32)
          val fUnit = Functor[F].map(Fix.un(f)) { a =>
            fields += a
            ()
          }
          p.pickle(fUnit)
          fields.foreach(pickle)

          ()
        }

        override def unpickle(implicit state: UnpickleState): Fix[F] = {
          val fUnit = p.unpickle
          Fix(Functor[F].map(fUnit)(_ => unpickle))
        }
      }
  }
}
