package ac.webapp.react

import ac.game.cards.Card
import ReactSyntax._

case class CardDisplay (card: Card, onClick: Callback) {
  def /> = CardDisplay.Component(this)
}

object CardDisplay {
  val Component = ScalaComponent.builder[CardDisplay]("CardDisplay")
    .render_P { props =>
      val card = props.card
      div(`class` := s"clickable-card card-${card.color.toString}", onClick --> props.onClick)(
        div(`class` := "title")(card.name),
        div(`class` := "description"),
        div(`class` := "picture"),
        div(`class` := "price")(card.worth),
      )
    }
    .build
}
