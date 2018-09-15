package ac.frontend

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, _}
import scala.scalajs.LinkingInfo

import cats.effect._
import slinky.web.ReactDOM
import slinky.hot
import org.scalajs.dom.document

@JSImport("resources/index.styl", JSImport.Default)
@js.native
object IndexCSS extends js.Object


@JSExportTopLevel("entrypoint")
object Main extends IOApp {
  //noinspection TypeAnnotation
  override implicit def timer: Timer[IO] = super.timer

  override implicit def contextShift: ContextShift[IO] = super.contextShift
  lazy val Instance: ConcurrentEffect[IO] = ConcurrentEffect[IO]

  def run(args: List[String]): IO[ExitCode] = for {
    _    <- IO(IndexCSS)
    _    <- if (LinkingInfo.developmentMode) IO { hot.initialize() }
            else IO.unit
    root <- IO { document.getElementById("root") }

    _ <- Store.app.discrete.evalMap { as =>
      IO { ReactDOM.render(App(as), root) }
    }.compile.drain
  } yield ExitCode.Success

  @JSExport
  def exec(): Unit = main(Array())
}
