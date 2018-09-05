package ac.console

import ac.game.session.{Registration, Session}
import cats.effect.{ExitCode, IO, IOApp}
import cats.effect.Console.io._

object Runner extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- putStrLn("Welcome to the")
      _ <- putStrLn("ARCOMAGE")
      _ <- putStrLn("(the console redaction)")
      reg <- Registration[IO]
      _ <- reg.enlist(new ConsoleParticipant)
      _ <- reg.enlist(new MrDerp[IO])
      _ <- Session.start(reg)
    } yield ExitCode.Success
}

