package ac.frontend

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

import slinky.core._
import slinky.core.annotations.react
import slinky.web.html._

@JSImport("resources/App.css", JSImport.Default)
@js.native
object AppCSS extends js.Object

@JSImport("resources/logo.svg", JSImport.Default)
@js.native
object ReactLogo extends js.Object

@react class App extends Component {
  case class Props(isGuest: Boolean)
  case class State ()

  def initialState: State = State()

  private val css = AppCSS

  def render() = {
    div(className := "App")(
      header(className := "App-header")(
        img(src := ReactLogo.asInstanceOf[String], className := "App-logo", alt := "logo"),
        h1(className := "App-title")("Welcome to React (with Scala.js and Slinky!)")
      ),
      if (props.isGuest) p("You are a guest") else p("You are a host"),
      p(className := "App-intro")(
        "To get started, edit ", code("App.scala"), " and save to reload."
      )
    )
  }
}
