package ac.frontend

import ac.frontend.states.StoreAlg
import cats.effect.Timer
import com.olegpy.shironeko.SlinkyConnector


object Store extends SlinkyConnector[StoreAlg] {
  trait HasTimer {
    implicit def timerFromAlgebra[F[_]: StoreAlg]: Timer[F] =
      StoreAlg[F].currentTimer
  }
}
