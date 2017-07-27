package ac.webapp.react

import ac.game.player.PlayerScope
import ac.interactions.Event
import ReactSyntax._

case class Battlefield (
  info: PlayerScope,
  trigger: Event => Callback,
  isEnemy: Boolean
) {
  def /> = Battlefield.Component(this)
}

object Battlefield {
  class Backend($: BackendScope[Battlefield, Unit]) {
    def render(p: Battlefield) = {
      import p.info._
      div(
        ResourcesColumn(playerName, game.stats.resources, game.stats.income)./>,
        ResourcesColumn(enemyName, game.enemy.resources, game.enemy.income)./>,
        EnemyCards(cards.hand.size)./>,
        BuildingsDisplay(
          game.stats.buildings,
          game.enemy.buildings,
          conditions.victoryConditions.tower
        )./>,
        PlayerCards(
          cards.hand
            .zipWithIndex
            .map { case (card, idx) => (card, p.trigger(Event.PlayedCard(idx))) }
        )./>
      )
    }
  }

  val Component = ScalaComponent.builder[Battlefield]("Battlefield")
    .renderBackend[Backend]
    .build
}
