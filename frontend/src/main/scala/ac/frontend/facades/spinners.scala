package ac.frontend.facades

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

import com.github.ghik.silencer.silent
import slinky.core.ExternalComponent
import slinky.core.annotations.react


object spinners {
  @react object ScaleLoader extends ExternalComponent {
    case class Props(height: Int, width: Int, margin: String, radius: Int)
    val component = internal.spinnersJS.ScaleLoader
  }
}