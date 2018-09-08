package ac.frontend.peering

import ac.frontend.utils
import cats.effect.Sync
import cats.implicits._

object HostGuest {
  val GuestKey = "ac_game"

  def guestToken[F[_]: Sync]: F[Option[String]] =
    utils.currentUrl[F].map(url =>
      utils.parseQueryString(url.search).get(GuestKey))
}
