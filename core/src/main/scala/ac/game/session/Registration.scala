package ac.game.session

import ac.game.flow.Participant
import cats.effect.{Async, IO, Sync}
import cats.effect.concurrent.{Deferred, Ref}
import cats._
import implicits._

trait Registration[F[_]] {
  def enlist(p: Participant[F]): F[Unit]
  def participants: F[(Participant[F], Participant[F])]
}

object Registration {
  def apply[F[_]: Async]: F[Registration[F]] = {
    val df = Deferred.uncancelable[F, Participant[F]]
    (df, df, Ref[F].of(0)).mapN(new Later[F](_, _, _))
  }

  def eager[F[_]: Sync](p1: Participant[F], p2: Participant[F]): Registration[F] =
    new Registration[F] {
      def enlist(p: Participant[F]): F[Unit] =
        Sync[F].raiseError(new RuntimeException("Cannot enlist participants"))

      def participants: F[(Participant[F], Participant[F])] = (p1, p2).pure[F]
    }

  private class Later[F[_]: Sync] (
    player1: Deferred[F, Participant[F]],
    player2: Deferred[F, Participant[F]],
    counter: Ref[F, Int]
  ) extends Registration[F] {
    def enlist(p: Participant[F]): F[Unit] =
      counter.get.flatMap {
        case 0 => player1.complete(p)
        case 1 => player2.complete(p)
        case _ => Sync[F].raiseError[Unit](new RuntimeException("Invalid state!"))
      } *> counter.update(_ + 1)

    def participants: F[(Participant[F], Participant[F])] =
      (player1.get, player2.get).tupled
  }
}
