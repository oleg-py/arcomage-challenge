package ac.frontend.states

import ac.frontend.actions.connect
import ac.frontend.utils
import cats.effect.Sync
import cats.implicits._
import monix.eval.Coeval
import monocle.macros.Lenses
import upickle.default._
import org.scalajs.dom.window.localStorage
import mouse.boolean._
import upickle.core.AbortException

trait PersistentSettings[F[_]] {
  def readAll: F[PersistentSettings.Repr]
  def writeAll(s: PersistentSettings.Repr): F[Unit]
  def modify(f: PersistentSettings.Repr => PersistentSettings.Repr): F[Unit]
}

object PersistentSettings {
  @Lenses case class Repr(
    name: String = "",
    email: String = "",
    tavern: String = "Harmondale",
    cards: Int = 6
  )

  object Repr {
    implicit def codec: ReadWriter[Repr] = macroRW
  }

  def forLocalStorage[F[_]](key: String)(implicit F: Sync[F]): PersistentSettings[F] = new PersistentSettings[F] {
    def readAll: F[Repr] = F.delay {
      Option(localStorage.getItem(key)).fold(Repr())(read[Repr](_))
    } recover {
      case _: AbortException => Repr()
    }

    def writeAll(s: Repr): F[Unit] = F.delay {
      localStorage.setItem(key, write(s))
    }

    def modify(f: Repr => Repr): F[Unit] = readAll.map(f).flatMap(writeAll)
  }

  def apply[F[_]](implicit F: Sync[F]): PersistentSettings[F] = {
    if (utils.isDevelopment) {
      connect.isGuest[Coeval].value().fold(
        forLocalStorage[F]("arcomage-guest-dev"),
        forLocalStorage[F]("arcomage-host-dev")
      )
    } else {
      forLocalStorage[F]("arcomage-user")
    }
  }
}
