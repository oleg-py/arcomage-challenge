package ac.webapp.react.styles

import scala.language.postfixOps
import scalacss.DevDefaults._

object Stylesheets extends StyleSheet.Inline {
  import dsl._

  val resourceBlockCommon = mixin(
    addClassName("resource"),
    width(100 px),
    height(100 px),
    border(solid, 3 px, black)
  )

  val bricks = style(
    resourceBlockCommon,
    backgroundColor.red
  )

  val gems = style(
    resourceBlockCommon,
    backgroundColor.blue
  )

  val recruits = style(
    resourceBlockCommon,
    backgroundColor.green
  )
}
