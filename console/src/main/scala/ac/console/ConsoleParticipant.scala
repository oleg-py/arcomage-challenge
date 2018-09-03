package ac.console

import scala.util.Try

import ac.game.Resources
import ac.game.cards.Card
import ac.game.flow._
import cats.effect.IO
import cats.effect.Console.io._
import cats.implicits._
import eu.timepit.refined.types.numeric.NonNegInt


class ConsoleParticipant extends Participant[IO] {
  def getTurn(hand: Vector[Card], rsc: Resources[NonNegInt]): IO[TurnIntent] =
    for {
      _ <- putStrLn("Your resources:")
      _ <- putStrLn(s"Bricks: ${rsc.bricks} / " +
        s"Gems: ${rsc.gems} / " +
        s"Recruits: ${rsc.recruits}")
      _ <- putStrLn("Your cards are:")
      _ <- hand.zipWithIndex.traverse_ { case (card, idx) =>
        /*_*/
        putStrLn(s"${idx + 1}. ${card.name}")
        /*_*/
      }
      _ <- putStrLn("Enter a number to play a card. Enter - in front of it to discard a card")
      line <- readLn
      ans = Try(line.toInt).getOrElse(0)
      abs = Math.abs(ans)
      ti <- if (abs == 0 || abs > hand.length)
              putStrLn("Wrong move!") *> getTurn(hand, rsc)
            else if (ans < 0 && Discard(NonNegInt.unsafeFrom(abs)).canPlay(hand, rsc))
              Discard(NonNegInt.unsafeFrom(abs))
            else if (Play(NonNegInt.unsafeFrom(abs)).canPlay(hand, rsc))
              Play(NonNegInt.unsafeFrom(abs))
            else
              putStrLn("Wrong move!") *> getTurn(hand, rsc)
    } yield ti

  def notify(notification: Notification): IO[Unit] = notification match {
    case TurnEnd => putStrLn("...at an end, your turn is")
    case Victory => putStrLn("You're a winner!!")
    case Defeat  => putStrLn("You're lose")
  }
}
