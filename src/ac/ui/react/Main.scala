package ac.ui.react

import ac.communication._
import ac.game.Randomizer
import ac.interactions.Result
import ac.syntax._
import monix.eval.{Task, TaskApp}
import boopickle.Default._

object Main extends TaskApp {
  override def runc = Task.defer {
    implicit val algebra =
      new PeerJS[EitherId[Result, Result]]("19e27tcfy8drt3xr")
        with UniqueId.ForTask
        with Collectable.TaskToObservable
        with Randomizer.Impure

    discard { algebra }
/*    val peer = new Peer(
      ???,
      new PeerJS("19e27tcfy8drt3xr")
    )*/
    Task.unit
  }
}
