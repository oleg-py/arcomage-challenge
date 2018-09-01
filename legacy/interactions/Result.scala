package ac.interactions

import ac.syntax._
import ac.game.player.CardScope
import cats.MonadState
import cats.data.StateT

sealed trait Result extends Product with Serializable {
  def process[F[_]: ErrM](outcomes: OutcomeFn[F]): Result.Processed[F]
}

object Result {
  type Processed[F[_]] = StateT[F, State, Option[Result]]

  private def S[F[_]](implicit ev: MonadState[StateT[F, State, ?], State]) = ev
  private def err(msg: String): Option[Result] = Some(Err(msg))

  case class Ack(expected: CardScope) extends Result {
    override def process[F[_] : ErrM](outcomes: OutcomeFn[F]): Processed[F] =
      for (current <- S[F].get) yield
        if (current.comparable contains expected) None
        else err("State acknowledgment failed")
  }

  case class Evt(cmd: Event) extends Result {
    override def process[F[_] : ErrM](outcomes: OutcomeFn[F]): Processed[F] = {
      def resultForMatch(outcome: F[(State, Option[Event])]) = for {
        outcomeS          <- StateT.lift(outcome)
        (state, maybeCmd) = outcomeS
        _                 <- S[F].set(state)
      } yield maybeCmd.map(Evt) orElse state.comparable.map(Ack)

      def totalResult(state: State) =
        (outcomes andThen resultForMatch) applyOrElse (
          (state, cmd),
          Function.const { S[F] pure err(s"Invalid command $cmd for state $state") }
        )

      S[F].get flatMap totalResult
    }
  }
  case class Err(msg: String) extends Result {
    override def process[F[_]: ErrM](outcomes: OutcomeFn[F]): Processed[F] =
      StateT.lift(ErrM[F].raiseError(new Exception(msg)))
  }
}
