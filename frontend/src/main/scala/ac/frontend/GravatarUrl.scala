package ac.frontend

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

import com.github.ghik.silencer.silent


@JSImport("gravatar-url", JSImport.Namespace)
@js.native
@silent object GravatarUrl extends js.Any {
  def apply(s: String, options: js.Object): String = js.native
}
