package ac.frontend.states

sealed trait RematchState

object RematchState {
  case object NotAsked extends RematchState
  case object Asking   extends RematchState
  case object Asked    extends RematchState
  case object Accepted extends RematchState
}
