package ac.frontend.components

import ac.frontend.Store
import ac.frontend.facades.AntDesign.Spin
import ac.frontend.states.StoreAlg
import monix.eval.Task
import slinky.core.facade.ReactElement
import slinky.web.html._


object ReconnectingOverlay extends Store.ContainerNoProps {
  type State = Boolean

  def subscribe(implicit F: StoreAlg[Task]): fs2.Stream[Task, Boolean] =
    F.peerConnection.listen.map(_.isDefined)


  def render(connected: Boolean)(implicit F: StoreAlg[Task]): ReactElement =
    if (connected) None
    else div(className := "error-overlay")(
      Spin("Waiting for the opponent to reconnect...")
    )
}
