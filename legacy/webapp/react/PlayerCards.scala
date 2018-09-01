package ac.webapp.react

import ac.game.cards.Card
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{Callback, ScalaComponent}

case class PlayerCards (cards: Vector[(Card, Callback)]) {
  def /> = PlayerCards.Component(this)
}

object PlayerCards {
  val Component = ScalaComponent.builder[PlayerCards]("PlayerCards")
    .render_P { props =>
      <.div(
        ^.cls := "player-cards",

        props.cards.zipWithIndex.toVdomArray { case ((card, onCard), idx) =>
          CardDisplay.Component.withKey(idx)(CardDisplay(card, onCard))
        }
      )
    }
    .build
}
