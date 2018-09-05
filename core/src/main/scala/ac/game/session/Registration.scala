package ac.game.session

import ac.game.flow.Participant
import cats.effect.{Async, IO, Sync}
import cats.effect.concurrent.{Deferred, Ref}
import cats._
import implicits._

class Registration[F[_]: Sync] private (
  player1: Deferred[F, Participant[F]],
  player2: Deferred[F, Participant[F]],
  counter: Ref[F, Int]
) {
  def enlist(p: Participant[F]): F[Unit] =
    counter.get.flatMap {
      case 0 => player1.complete(p)
      case 1 => player2.complete(p)
      case _ => Sync[F].raiseError[Unit](new RuntimeException("Invalid state!"))
    } *> counter.update(_ + 1)

  def participants: F[(Participant[F], Participant[F])] =
    (player1.get, player2.get).tupled
}

object Registration {
  def apply[F[_]: Async]: F[Registration[F]] = {
    val df = Deferred.uncancelable[F, Participant[F]]
    (df, df, Ref[F].of(0)).mapN(new Registration(_, _, _))
  }
}
