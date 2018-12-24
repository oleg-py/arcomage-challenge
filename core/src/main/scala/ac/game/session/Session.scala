package ac.game.session

import ac.game.cards.Cards
import ac.game.flow._, TurnIntent._, Notification._
import ac.game.player.CardScope
import ac.game.{GameConditions, VictoryConditions}
import cats.effect.Sync
import cats.effect.concurrent.Ref
import cats.implicits._

class Session[F[_]: Sync] private (
  p1: Participant[F],
  p2: Participant[F],
  cards1: Ref[F, Cards],
  cards2: Ref[F, Cards],
  state: Ref[F, CardScope],
  conds: VictoryConditions
) {

  private def getIntent(recheck: Boolean = false): F[TurnIntent] =
    for {
      cards <- cards1.get.map(_.hand)
      res <- state.get.map(_.stats.resources)
      ti <- p1.getValidIntent(cards, res)
      mustDiscard <- state.get.map(_.requireDiscard)
      checked <- ti match {
        case t @ Discard(idx) if idx.value < cards.length => t.pure[F]
        case t @ Play(idx) if idx.value < cards.length && !mustDiscard => t.pure[F]
        case _ => getIntent(recheck = true)
      }
    } yield checked

  private def notifyResources: F[Unit] =
    for {
      gs <- state.get
      _ <- p1.notify(ResourceUpdate(gs))
      _ <- p2.notify(ResourceUpdate(gs.reverse))
    } yield ()

  private def isEndgame: F[Boolean] =
    state.get.map(s => conds.isVictory(s) || conds.isVictory(s.reverse))

  private def notifyEndgame: F[Unit] =
    state.get.map(conds.status).flatMap {
      case Some(value) => p1.notify(value.asNotification) *>
        p2.notify(value.inverse.asNotification)
      case None => ().pure[F]
    }

  private def turn(noIncome: Boolean = false) =
    state.update(CardScope.stats.modify(_.receiveIncome)).unlessA(noIncome) *>
    List(p1, p2).traverse_(_ notify Income).unlessA(noIncome) *>
    notifyResources *> isEndgame.ifM(
      notifyEndgame,
      p1.notify(TurnStart) *>
      getIntent().flatMap {
        case Discard(idx) =>
          cards1.modify(_.pull(idx.value).swap)
            .flatMap { card =>
              p2.notify(EnemyPlayed(card, discarded = true)) *>
              p1.notify(CardPlayed(idx, discarded = true)) *>
              cards1.get.map(_.hand).map(HandUpdated).flatMap(p1.notify)
            }
        case Play(idx) =>
          cards1.modify(_.pull(idx.value).swap)
            .flatMap { card =>
              state.update(CardScope.stats.modify(_.addResources(-card.cost))) *>
              p2.notify(EnemyPlayed(card, discarded = false)) *>
              p1.notify(CardPlayed(idx, discarded = false)) *>
              state.update(card) *>
              cards1.get.map(_.hand).map(HandUpdated).flatMap(p1.notify)
            }
      } *> notifyResources
    )

  private def continuation: F[Unit] =
    isEndgame
      .ifM(
        turn(),
        state.get
          .map(!_.passTurn)
          .ifM(
            state.update(CardScope.turnMods.modify(_.drop(1))) *> turn(true) *> continuation,
            p1.notify(TurnEnd) *> swap.flatMap(s => s.turn() *> s.continuation)
          )
      )


  private def loop = turn(noIncome = true) *> continuation

  private def swap = state.update(_.reverse)
    .as(new Session(p2, p1, cards2, cards1, state, conds))
}

object Session {
  def start[F[_]: Sync](reg: Registration[F]): F[Unit] = {
    for {
      (p1, p2) <- reg.participants
      conds <- p2.proposeConditions
      cards1 <- bootstrap(conds, p1)
      cards2 <- bootstrap(conds, p2)
      gameState <- Ref[F].of(conds.initialState)
      session = new Session(p1, p2, cards1, cards2, gameState, conds.victoryConditions)
      _ <- session.loop
    } yield ()
  }

  private def bootstrap[F[_]: Sync](conds: GameConditions, p: Participant[F]) = {
    Cards.initial[F](conds.handSize)
      .flatTap(x => p.notify(HandUpdated(x.hand)))
      .flatTap(_ => p.notify(GameStart))
      .flatMap(Ref[F].of(_))
  }
}
