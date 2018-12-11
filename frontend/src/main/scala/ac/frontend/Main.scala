package ac.frontend

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, _}
import scala.scalajs.LinkingInfo

import cats.effect._
import monix.eval.{Task, TaskApp}
import monix.execution.Scheduler
import slinky.web.ReactDOM
import slinky.hot
import org.scalajs.dom.document

@JSImport("resources/index.styl", JSImport.Default)
@js.native
object IndexCSS extends js.Object


@JSExportTopLevel("entrypoint")
object Main extends TaskApp {
  lazy val Instance: ConcurrentEffect[Task] = ConcurrentEffect[Task]

  override def scheduler: Scheduler = super.scheduler

  def run(args: List[String]): Task[ExitCode] = Task {
    locally(IndexCSS)
    if (LinkingInfo.developmentMode) hot.initialize()
    val root = document.getElementById("root")
    ReactDOM.render(ErrorDisplay(App()), root)
    ExitCode.Success
  }

  @JSExport
  def exec(): Unit = main(Array())
}
