package ac.frontend

import scala.concurrent.duration.TimeUnit
import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, _}
import scala.scalajs.LinkingInfo

import ac.frontend.actions.{connect, matches}
import ac.frontend.facades.{AntDesign, Peer}
import ac.frontend.states.StoreAlg
import ac.frontend.utils.bundle
import cats.effect._
import com.olegpy.shironeko.StoreDSL
import monix.eval.{Task, TaskApp}
import monix.execution.{Cancelable, ExecutionModel, Scheduler}
import monix.execution.schedulers.ReferenceScheduler
import slinky.web.ReactDOM
import slinky.hot
import org.scalajs.dom.document

@JSImport("resources/index.styl", JSImport.Default)
@js.native object IndexCSS extends js.Any


@JSExportTopLevel("entrypoint")
object Main extends TaskApp {
  def run(args: List[String]): Task[ExitCode] = Task.defer {
    bundle(IndexCSS, AntDesign.CSS)

    if (LinkingInfo.developmentMode) hot.initialize()

    for {
      peer <- Peer[Task].start
      root <- Task { document.getElementById("root") }
      implicit0(alg: StoreAlg[Task]) <- StoreDSL[Task].use { implicit dsl =>
        Task.pure(new StoreAlg(peer.join))
      }
      _    <- alg.installHandler
      _    <- matches.bootstrapRematching
      _    <- connect.preinitIfGuest.start
      _    <- Task {
        ReactDOM.render(Store[Task](ErrorDisplay(App())), root)
      }
    } yield ExitCode.Success
  }

  @JSExport
  def exec(): Unit = main(Array())

  override def scheduler: Scheduler = new ReferenceScheduler {
    private[this] val global = Main.super.scheduler
    def execute(command: Runnable): Unit = global.execute(command)
    def reportFailure(t: Throwable): Unit = {
      global.reportFailure(t)
      algebra.foreach {
        _.error.set(Some(t.getMessage)).runAsyncAndForget(this)
      }
    }
    def scheduleOnce(initialDelay: Long, unit: TimeUnit, r: Runnable): Cancelable =
      global.scheduleOnce(initialDelay, unit, r)

    def executionModel: ExecutionModel = global.executionModel
  }

  private[this] var algebra: Option[StoreAlg[Task]] = None
}
