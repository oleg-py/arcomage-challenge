package ac.frontend.utils

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

import com.github.ghik.silencer.silent


@JSImport("clipboard", JSImport.Default)
@js.native
@silent
class ClipboardJS (selector: String) extends js.Object {
  def destroy(): Unit = js.native
}
