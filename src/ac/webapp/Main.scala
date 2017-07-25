package ac.webapp

import ac.communication.PeerJS
import ac.interactions.{Result, Transitions}
import ac.syntax.discard
import boopickle.Default._
import japgolly.scalajs.react.Callback
import monix.cats._
import monix.eval.{Task, TaskApp}
import org.scalajs.dom.document

object Main extends TaskApp with Algebras {
  override def runc = Task.deferAction { implicit sc =>
    new Connection(new PeerJS[Result]("19e27tcfy8drt3xr"), Transitions[Task])
      .open
      .map { case (states, onCmd) =>
        val logged = states
          .doOnError(println)
          .doOnNext(s => println(s"App transitioned to state $s"))

        def register(fn: App.State => Callback) =
          discard { logged.foreach(s => fn(s).attemptTry.runNow().get) }

        val target = document.getElementById("app-root")

//        Stylesheets.addToDocument()

        App(onCmd, register)./>
          .renderIntoDOM(target)

        ()
      }
  }



}
