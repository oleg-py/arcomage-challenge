package ac.webapp

import ac.communication.PeerJS
import ac.interactions._
import ac.syntax._
import boopickle.Default._
import japgolly.scalajs.react.Callback
import monix.eval.{Task, TaskApp}
import org.scalajs.dom.document

object Main extends TaskApp {
  override def runc = Task.deferAction { implicit sc =>
    val (states, onCommand) = connect(new PeerJS[Result]("19e27tcfy8drt3xr"))
    val target = document.getElementById("app-root")

    def register(fn: RootState => Callback) =
      discard { states.foreach(s => fn(s).attemptTry.runNow().get) }

    RootComponent.Props(onCommand, register)
      .render
      .renderIntoDOM(target)

    Task.unit
  }
}
