package ac.webapp.react

import ReactSyntax._


case class EnemyCards (n: Int) {
  def /> = EnemyCards.Component(this)
}

object EnemyCards {
  val Component = ScalaComponent.builder[EnemyCards]("EnemyCards")
    .render_P { props =>
      div(`class` := "enemy-cards")(
        (1 to props.n).toVdomArray(i => div(`class` := "enemy-card"))
      )
    }
    .build
}
