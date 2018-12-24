package ac.frontend.facades.internal

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

import ac.frontend.facades.internal
import com.github.ghik.silencer.silent


@JSImport("react-spinners", JSImport.Default)
@js.native
@silent
object spinnersJS extends js.Object {
  val CircleLoader: js.Object = js.native
  val ScaleLoader: js.Object = js.native
}
