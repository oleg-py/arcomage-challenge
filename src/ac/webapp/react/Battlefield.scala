package ac.webapp.react

import ac.game.player.PlayerScope
import ac.interactions.Event
import japgolly.scalajs.react._
import vdom.all._


case class Battlefield (
  info: PlayerScope,
  trigger: Event => Callback,
  isEnemy: Boolean
) {
  def /> = Battlefield.Component(this)()
}

object Battlefield {
  class Backend($: BackendScope[Battlefield, Unit]) {
    def render(p: Battlefield) = {
      import p.info._
      div("TODO"/*
        ResourcesColumn(playerName, game.stats.resources, game.stats.income)./>,
        ResourcesColumn(enemyName, game.enemy.resources, game.enemy.income)./>,
        EnemyCards(cards.hand.size)./>,
        Towers(game.stats.buildings, game.enemy.buildings)./>,
        PlayerCards(cards.hand, visible = p.isEnemy)./>
      */)
    }
  }

  val Component = ScalaComponent.builder[Battlefield]("Battlefield")
    .renderBackend[Backend]
    .build
}
