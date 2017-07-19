package ac.webapp

import ac.interactions.Result


sealed trait PeerCommand

object PeerCommand {
  case object Host extends PeerCommand
  case class Join(offer: String) extends PeerCommand
  case class Run(cmd: Result) extends PeerCommand
}

