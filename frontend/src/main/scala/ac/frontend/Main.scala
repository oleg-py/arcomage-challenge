package ac.frontend

import scala.concurrent.duration.TimeUnit
import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, _}
import scala.scalajs.LinkingInfo

import ac.frontend.actions.{connect, matches}
import cats.syntax.all._
import cats.effect._
import monix.eval.{Task, TaskApp}
import monix.execution.{Cancelable, ExecutionModel, Scheduler}
import monix.execution.schedulers.ReferenceScheduler
import slinky.web.ReactDOM
import slinky.hot
import org.scalajs.dom.document

@JSImport("resources/index.styl", JSImport.Default)
@js.native
object IndexCSS extends js.Object


@JSExportTopLevel("entrypoint")
object Main extends TaskApp {
  lazy val Instance: ConcurrentEffect[Task] = ConcurrentEffect[Task]

  override def scheduler: Scheduler = new ReferenceScheduler {
    private[this] val global = Main.super.scheduler
    def execute(command: Runnable): Unit = global.execute(command)
    def reportFailure(t: Throwable): Unit = {
      global.reportFailure(t)
      Store.error.set(Some(t.getMessage)).runAsyncAndForget(this)
    }
    def scheduleOnce(initialDelay: Long, unit: TimeUnit, r: Runnable): Cancelable =
      global.scheduleOnce(initialDelay, unit, r)

    def executionModel: ExecutionModel = global.executionModel
  }

  def run(args: List[String]): Task[ExitCode] = Task.defer {
    locally(IndexCSS)
    if (LinkingInfo.developmentMode) hot.initialize()
    val root = document.getElementById("root")
    ReactDOM.render(ErrorDisplay(App()), root)

    matches.bootstrapRematching(Store) *>
    connect.preinitIfGuest(Store).start.as(ExitCode.Success)
  }

  @JSExport
  def exec(): Unit = main(Array())
}
