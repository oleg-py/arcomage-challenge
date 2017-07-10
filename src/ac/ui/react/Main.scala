package ac.ui.react

import ac.communication.{Discriminated, PeerJS}
import ac.syntax._
import boopickle.Default._
import monix.eval.{Task, TaskApp}

object Main extends TaskApp {
  override def runc = for {
    _ <- Task.unit
    msg = new Discriminated[String, String](
      new PeerJS("19e27tcfy8drt3xr"),
      a => Task.pure(a))
    sc <- Task.scheduler
    props = Arcomage.Props(msg, sc)
  } yield ()
}
