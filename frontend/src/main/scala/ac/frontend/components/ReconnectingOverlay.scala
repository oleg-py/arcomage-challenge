package ac.frontend.components

import ac.frontend.Store
import ac.frontend.facades.AntDesign.Spin
import ac.frontend.states.StoreAlg
import cats.effect.Concurrent
import com.olegpy.shironeko.interop.Exec
import slinky.core.facade.ReactElement
import slinky.web.html._


object ReconnectingOverlay extends Store.ContainerNoProps {
  type State = Boolean

  def subscribe[F[_]: Concurrent: StoreAlg]: fs2.Stream[F, State] =
    StoreAlg[F].peerConnection.discrete.map(_.isDefined)

  def render[F[_]: Concurrent: StoreAlg: Exec](connected: Boolean): ReactElement =
    if (connected) None
    else div(className := "error-overlay")(
      Spin("Waiting for the opponent to reconnect...")
    )
}
