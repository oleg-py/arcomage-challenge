package ac.game.session

import ac.game.{GameConditions, VictoryConditions}
import ac.game.cards.{Card, Cards}
import ac.game.flow._
import ac.game.player.{CardScope, TurnMod}
import ac.syntax._
import cats.effect.Sync
import cats._
import cats.effect.concurrent.Ref
import implicits._

class Session[F[_]: Sync] private (
  p1: Participant[F],
  p2: Participant[F],
  cards1: Ref[F, Cards],
  cards2: Ref[F, Cards],
  state: Ref[F, CardScope],
  conds: VictoryConditions
) {

  private def getIntent: F[TurnIntent] = ??? // P1, check if must discard
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
      getIntent.flatMap {
        case Discard(idx) =>
          cards1.modify(_.pull(idx.value).swap)
            .flatMap { card =>
              p2.notify(EnemyPlayed(card, discarded = true)) *>
                p1.notify(CardPlayed(card, discarded = true))
            }
        case Play(idx) =>
          cards1.modify(_.pull(idx.value).swap)
            .flatMap { card =>
              p2.notify(EnemyPlayed(card, discarded = false)) *>
                p1.notify(CardPlayed(card, discarded = false)) *>
                state.update(card)
            }
      } *> notifyResources
    )

  // TODO: return on victory
  private def continuation: F[Unit] = state.get
    .map(_.turnMods.headOption.contains(TurnMod.PlayAgain))
    .ifM(
      state.update(CardScope.turnMods.modify(_.drop(1))) *> continuation,
      swap.flatMap(s => s.turn() *> s.continuation)
    )


  private def loop = turn(firstTurn = true) *> continuation

  private def swap = state.update(_.reverse)
    .map(new Session(p2, p1, cards2, cards1, _, conds))
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
      .flatTap(_.hand map CardReceived traverse_ p.notify)
      .flatTap(_ => p.notify(GameStart))
      .flatMap(Ref[F].of(_))
  }
}
