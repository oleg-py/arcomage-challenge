package ac.frontend
import slinky.core.facade.ReactElement
import slinky.web.html._

object ErrorDisplay {
  def apply[A](onSuccess: => ReactElement): ReactElement =
    new Store.Container(Store.error.listen) {
      def render(a: Option[String]): ReactElement = a match {
        case Some(msg) => div(className := "error-dialog box")(msg)
        case None => onSuccess
      }
    }.apply()
}
