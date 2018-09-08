package ac.frontend

import monocle.macros.Lenses


@Lenses
case class AppState (
  hostKey: String = "",
  isGuest: Boolean = false,
  message: String = "",
  history: Vector[String] = Vector()
)
