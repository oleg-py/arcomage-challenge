package ac.frontend.states

import ac.frontend.actions.connect
import ac.frontend.utils
import cats.effect.Sync
import cats.implicits._
import io.circe.generic.auto._
import io.circe.parser.decode
import io.circe.refined._
import io.circe.syntax._
import monix.eval.Coeval
import monocle.macros.Lenses
import org.scalajs.dom.window.localStorage
import mouse.boolean._

trait PersistentSettings[F[_]] {
  def readAll: F[PersistentSettings.Repr]
  def writeAll(s: PersistentSettings.Repr): F[Unit]
  def modify(f: PersistentSettings.Repr => PersistentSettings.Repr): F[Unit]
}

object PersistentSettings {
  @Lenses case class Repr(
    name: String = "",
    email: String = "",
    conditionsChoice: ConditionsChoice = ConditionsChoice(),
  )

  def forLocalStorage[F[_]](key: String)(implicit F: Sync[F]): PersistentSettings[F] = new PersistentSettings[F] {
    def readAll: F[Repr] = F.delay {
      Option(localStorage.getItem(key))
        .flatMap(decode[Repr](_).toOption)
        .getOrElse(Repr())
    }

    def writeAll(s: Repr): F[Unit] = F.delay {
      localStorage.setItem(key, s.asJson.noSpaces)
    }

    def modify(f: Repr => Repr): F[Unit] = readAll.map(f).flatMap(writeAll)
  }

  def apply[F[_]](implicit F: Sync[F]): PersistentSettings[F] = {
    if (utils.inDevelopment()) {
      connect.isGuest[Coeval].value().fold(
        forLocalStorage[F]("arcomage-guest-dev"),
        forLocalStorage[F]("arcomage-host-dev")
      )
    } else {
      forLocalStorage[F]("arcomage-user")
    }
  }
}
