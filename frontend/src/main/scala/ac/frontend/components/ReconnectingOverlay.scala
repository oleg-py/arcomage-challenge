package ac.frontend.components

import ac.frontend.Store
import ac.frontend.facades.AntDesign.Spin
import slinky.core.facade.ReactElement
import slinky.web.html._


object ReconnectingOverlay extends Store.Container(
  Store.peerConnection.listen.map(_.isDefined)
){
  def render(connected: Boolean): ReactElement =
    if (connected) None
    else div(className := "error-overlay")(
      Spin("Waiting for the opponent to reconnect...")
    )
}
