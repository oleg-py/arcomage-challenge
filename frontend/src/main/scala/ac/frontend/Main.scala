package ac.frontend

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, _}
import scala.scalajs.LinkingInfo

import ac.frontend.connections.HostGuest
import cats.effect.{ExitCode, IO, IOApp}
import slinky.web.ReactDOM
import slinky.hot
import org.scalajs.dom

@JSImport("resources/index.css", JSImport.Default)
@js.native
object IndexCSS extends js.Object


@JSExportTopLevel("entrypoint")
object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] = for {
    _    <- IO(IndexCSS)
    _    <- if (LinkingInfo.developmentMode) IO { hot.initialize() }
            else IO.unit
    root <- IO { dom.document.getElementById("root") }
    guestKey <- HostGuest.guestToken[IO]
    _    <- IO { ReactDOM.render(App(guestKey.nonEmpty), root) }
  } yield ExitCode.Success

  @JSExport
  def exec(): Unit = this.main(Array())
}
