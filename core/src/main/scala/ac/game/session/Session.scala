package ac.game.session

import ac.game.cards.Cards
import ac.game.flow._
import TurnIntent._
import Notification._
import ac.game.player.CardScope
import ac.game.{GameConditions, VictoryConditions}
import cats.data.OptionT
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

  private val notifyResources: F[Unit] =
    for {
      gs <- state.get
      _ <- p1.notify(ResourceUpdate(gs))
      _ <- p2.notify(ResourceUpdate(gs.reverse))
    } yield ()

  private val endGameOr =
    OptionT(state.get.map(conds.status))
    .semiflatMap { evt =>
      p1.notify(evt) *> p2.notify(evt.inverse)
    }
    .getOrElseF _

  private val performTurn: F[Unit] =
    for {
      ti   <- getIntent()
      card <- cards1.modify(_.pull(ti.idx.value).swap)
      _    <- state.update(CardScope.payCost(card)) unlessA ti.isDiscard
      _    <- p2.notify(EnemyPlayed(card, ti.isDiscard))
      _    <- p1.notify(CardPlayed(ti.idx, ti.isDiscard))
      _    <- state.update(card) unlessA ti.isDiscard
      _    <- cards1.get.map(_.hand).map(HandUpdated).flatMap(p1.notify)
    } yield ()

  private def step(noIncome: Boolean = false): F[Unit] =
    state.update(CardScope.stats.modify(_.receiveIncome)).unlessA(noIncome) *>
    List(p1, p2).traverse_(_ notify Income).unlessA(noIncome) *>
    notifyResources *>
    endGameOr(p1.notify(TurnStart) *> performTurn *> notifyResources)

  private def continuation: F[Unit] = endGameOr(
    state.get.map(_.passTurn).ifM(
      p1.notify(TurnEnd) *> swap.flatMap(s => s.step() *> s.continuation),
      state.update(CardScope.turnMods.modify(_.drop(1))) *> step(true) *> continuation
    )
  )


  private def loop = step(noIncome = true) *> continuation

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
