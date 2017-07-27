package ac.webapp.react

import ReactSyntax._
import ac.game.cards.Card

case class PlayerCards (cards: Vector[(Card, Callback)]) {
  def /> = PlayerCards.Component(this)
}

object PlayerCards {
  val Component = ScalaComponent.builder[PlayerCards]("PlayerCards")
    .render_P { props =>
      div(`class` := "player-cards")(
        props.cards.zipWithIndex.toVdomArray { case ((card, onCard), idx) =>
          CardDisplay.Component.withKey(idx)(CardDisplay(card, onCard))
        }
      )
    }
    .build
}
