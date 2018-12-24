package ac.frontend.facades

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

import com.github.ghik.silencer.silent
import slinky.core.ExternalComponent
import slinky.core.annotations.react


object spinners {
  @react object CircleLoader extends ExternalComponent {
    case class Props(size: Int, color: js.UndefOr[String] = js.undefined)
    val component = internal.spinnersJS.CircleLoader
  }

  @react object ScaleLoader extends ExternalComponent {
    case class Props(height: Int, width: Int, margin: String, radius: Int)
    val component = internal.spinnersJS.ScaleLoader
  }
}