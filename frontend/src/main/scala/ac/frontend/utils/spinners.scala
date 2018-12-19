package ac.frontend.utils

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

import com.github.ghik.silencer.silent
import slinky.core.ExternalComponent
import slinky.core.annotations.react


@JSImport("react-spinners", JSImport.Default)
@js.native
@silent
private[utils] object spinnersJS extends js.Object {
  val CircleLoader: js.Object = js.native
  val ScaleLoader: js.Object = js.native
}

object spinners {
  @react object CircleLoader extends ExternalComponent {
    case class Props(size: Int)
    val component = spinnersJS.CircleLoader
  }

  @react object ScaleLoader extends ExternalComponent {
    case class Props(height: Int, width: Int, margin: String, radius: Int)
    val component = spinnersJS.ScaleLoader
  }
}