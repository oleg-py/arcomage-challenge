package ac.messaging

package object protocol {
  type Numbered[A] = (Int, A)
  type Message[Req, Res] = Either[Numbered[Req], Numbered[Res]]

  type RawCommunicator[A] = Communicator[A, A]
  type BiCommunicator[Req, Res] = RawCommunicator[Message[Req, Res]]
}
