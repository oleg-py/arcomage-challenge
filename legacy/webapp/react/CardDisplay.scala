package ac.webapp.react

import ac.game.cards.Card
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{Callback, ScalaComponent}

case class CardDisplay (card: Card, onClick: Callback) {
  def /> = CardDisplay.Component(this)
}

object CardDisplay {
  val Component = ScalaComponent.builder[CardDisplay]("CardDisplay")
    .render_P { props =>
      val card = props.card
      <.div(
        ^.cls := s"clickable-card card-${card.color.toString}",
        ^.onClick --> props.onClick,

        <.div(
          ^.cls := "title",
          card.name
        ),
        <.div(
          ^.cls := "description",
          "Dummy description"
        ),
        <.div(
          ^.cls := "picture"
        ),
        <.div(
          ^.cls := "price",
          card.worth
        )
      )
    }
    .build
}
