package ac.ui.components

import mhtml._
import monix.reactive.Observable
import org.scalajs.dom._
import scala.concurrent.duration._

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import scala.xml.Elem

object Main extends JSApp {

  val hw: Var[Elem] = Var {
    <div>
      Hello, user!
      <button onclick={ () => window.alert("Hello from Scala.JS!") }>Confirm</button>
    </div>
  }

  @JSExport
  override def main(): Unit = {
    mount(document.body, hw)
  }
}
