package ac.webapp.react

import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.vdom.html_<^._


case class EnemyCards (n: Int) {
  def /> = EnemyCards.Component(this)
}

object EnemyCards {
  val Component = ScalaComponent.builder[EnemyCards]("EnemyCards")
    .render_P { props =>
      <.div(
        ^.cls := "enemy-cards",
        (1 to props.n).toVdomArray(i => <.div(^.cls := "enemy-card"))
      )
    }
    .build
}
