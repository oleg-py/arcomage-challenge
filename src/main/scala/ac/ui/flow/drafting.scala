package ac.ui.flow

import ac.messaging.Game
import ac.messaging.protocol.{Client, Message, Peer}
import ac.model.GameConditions
import monix.eval.Task
import org.scalajs.dom._
import window._

class Session[Request, Response](
  client: Client[Message[Request, Response]],
  site: String,
  peerId: Option[String]
) {
  def shareLink(inviteName: String) = s"$site?to=${client.id}&name=$inviteName"

  def awaitAsHost: Task[Peer[Request, Response]] =
    client.connectionRequests.firstL.map(new Peer(_))

  def awaitAsGuest: Option[Task[Peer[Request, Response]]] =
    peerId.map(id => client.connect(id).map(new Peer(_)))

  def await: Task[Peer[Request, Response]] = awaitAsGuest getOrElse awaitAsHost
}

sealed trait Flow[Self, Data, Next] { this: Self =>
  type Iteration = Task[Either[Self, Next]]
  def advance(data: Data): Task[Either[Self, Next]]
}

object RunACCommand {
  val f: Game.Command => Task[Game.Event] = ???
}

object Flow {
  val start: Flow[Nothing, String, SetupConditions] =
    (name: String) => Task.pure(Right(SetupConditions(name)))

  case class SetupConditions (playerName: String)
    extends Flow[SetupConditions, GameConditions, AwaitStartup] {
    override def advance(data: GameConditions): Iteration =
      Task.pure(Right(AwaitStartup(playerName, data)))
  }

  case class AwaitStartup(playerName: String, conds: GameConditions)
    extends Flow[AwaitStartup, Peer[Game.Command, Game.Event], Play_!] {
    override def advance(peer: Peer[Game.Command, Game.Event]): Iteration = for {
      _ <- peer.receive(RunACCommand.f)
      _ <- peer.request(Game.SetupConditions(conds))
    } yield Right(Play_!(conds, peer))
  }

  case class Play_!(args: Any*)
  case object Victory
}
