package ac.frontend.utils

import scala.scalajs.js


case class JSException(e: js.Error) extends RuntimeException(e.message) {
  override def getMessage: String = super.getMessage
  override def toString: String = s"${e.name} (from JS): ${e.message}"
  def dynamic: js.Dynamic = e.asInstanceOf[js.Dynamic]
}
