package ac.console

import ac.game.GameConditions
import ac.game.flow.{Participant, ResourceUpdate}
import ac.game.player.CardScope
import cats.effect.{ExitCode, IO, IOApp}
import cats.effect.Console.io._
import cats.effect.concurrent.Ref
import cats.implicits._

object Runner extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- putStrLn("Welcome to the")
      _ <- putStrLn("ARCOMAGE")
      _ <- putStrLn("(the console redaction)")
      player = new ConsoleParticipant
      bot = new MrDerp[IO]
    } yield ExitCode.Success
}


class Session (
  ref: Ref[IO, CardScope]
) {
  def turn(p1: Participant[IO], p2: Participant[IO]) =
    for {
      _ <- ref.update(CardScope.stats.modify(_.receiveIncome))
      _ <- ref.get.map(ResourceUpdate) >>= p2.notify
      _ <- p1.
    }
}
