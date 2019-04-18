package ac.frontend.actions

import ac.frontend.states.AppState.{AwaitingConditions, SupplyingConditions}
import ac.frontend.states._
import ac.game.session.{Registration, Session}
import cats.effect.Concurrent
import cats.effect.concurrent.Ref
import cats.effect.syntax.all._
import cats.syntax.all._
import fs2.Stream


object matches {
  def begin[F[_]: Concurrent](swap: Boolean = false)(implicit Store: StoreAlg[F]): F[Unit] = {
    val lp = new LocalParticipant[F]()
    val rp = new RemoteParticipant[F]()
    val reg = Registration.eager[F](if (swap) rp else lp, if (swap) lp else rp)
    Session.start(reg).onError {
      case ex => Store.fail(ex.getMessage)
    }.start.void
  }

  def bootstrapRematching[F[_]: Concurrent](implicit Store: StoreAlg[F]): F[Unit] = {
    val doRematch = connect.isGuest[F].map(!_)
      .mproduct(Ref[F].of)
      .map { case (isHost, myConditions) =>
        Store.rematchState.set(RematchState.NotAsked) *>
        myConditions.modify(b => (!b, b))
          .flatTap { swap => begin(swap) whenA isHost }
          .flatMap {
            case true  =>
              Store.app.set(SupplyingConditions)
            case false =>
              Store.app.set(AwaitingConditions)
          }
      }

    Stream.eval(doRematch).flatMap { action =>
      Store.rematchState.discrete
        .filter(_ == RematchState.Accepted)
        .evalMap(_ => action)
    }.compile.drain.start.void
  }

  def proposeRematch[F[_]: Concurrent](implicit Store: StoreAlg[F]): F[Unit] = {
    Store.rematchState.update {
      case RematchState.Asked => RematchState.Accepted
      case _                  => RematchState.Asking
    } >> Store.myTurn.set(false) >> Store.send(RematchRequest)
  }
}
