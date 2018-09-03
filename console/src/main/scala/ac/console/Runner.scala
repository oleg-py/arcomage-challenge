package ac.console

import cats.effect.{ExitCode, IO, IOApp}
import cats.effect.Console.io._
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
