package ac.game.session

import ac.game.cards.Cards
import ac.game.flow._, TurnIntent._, Notification._
import ac.game.player.{CardScope, TurnMod}
import ac.game.{GameConditions, VictoryConditions}
import ac.syntax._
import cats._
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
      _ <- (mustDiscard && !recheck)
        .ifA(state.update(CardScope.turnMods.modify(_.drop(1))))
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
    state.get.map(conds.isVictory).ifM(
      p1.notify(Victory) *> p2.notify(Defeat),
      p1.notify(Defeat) *> p2.notify(Victory)
    )

  private def turn(firstTurn: Boolean = false) =
    (!firstTurn).ifA {
      state.update(CardScope.stats.modify(_.receiveIncome))
    } *> notifyResources *> isEndgame.ifM(
      notifyEndgame,
      getIntent().flatMap {
        case Discard(idx) =>
          cards1.modify(_.pull(idx.value).swap)
            .flatMap { card =>
              cards1.get.map(_.hand).map(HandUpdated).flatMap(p1.notify) *>
              p2.notify(EnemyPlayed(card, discarded = true)) *>
              p1.notify(CardPlayed(card, discarded = true))
            }
        case Play(idx) =>
          cards1.modify(_.pull(idx.value).swap)
            .flatMap { card =>
              cards1.get.map(_.hand).map(HandUpdated).flatMap(p1.notify) *>
              p2.notify(EnemyPlayed(card, discarded = false)) *>
              p1.notify(CardPlayed(card, discarded = false)) *>
              state.update(card)
            }
      } *> notifyResources
    )

  private def continuation: F[Unit] =
    isEndgame
      .ifM(
        turn(),
        state.get
          .map(_.turnMods.headOption.contains(TurnMod.PlayAgain))
          .ifM(
            state.update(CardScope.turnMods.modify(_.drop(1))) *> turn() *> continuation,
            swap.flatMap(s => s.turn() *> s.continuation)
          )
      )


  private def loop = turn(firstTurn = true) *> continuation

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
