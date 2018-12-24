package ac.frontend.components

import ac.frontend.Store
import slinky.core.facade.ReactElement
import slinky.web.html._
import ac.frontend.facades.spinners.CircleLoader


object ReconnectingOverlay extends Store.Container(
  Store.peerConnection.listen.map(_.isDefined)
){
  def render(connected: Boolean): ReactElement =
    if (connected) None
    else div(className := "error-overlay")(
      CircleLoader(128, "white"),
      span("Waiting for the opponent to reconnect...")
    )
}
