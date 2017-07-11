package ac.ui.react

import ac.communication.{Peer, PeerJS}
import ac.syntax._
import boopickle.Default._
import monix.eval.{Task, TaskApp}

object Main extends TaskApp {
  override def runc = Task.defer {
    val peer = new Peer(
      ???,
      new PeerJS("19e27tcfy8drt3xr")
    )
    Task.unit
  }
}
