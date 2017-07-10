package ac.ui.react


import ac.communication.{Discriminated, Offer}
import ac.syntax._
import japgolly.scalajs.react._
import vdom.html_<^._
import cats.syntax.option._
import monix.execution.Scheduler

object Arcomage {
  case class Props (
    messenger: Discriminated[String, String],
    scheduler: Scheduler
  )

  case class State (
    myOffer: String = "",
    hostOffer: String = "",
    messageText: String = "",
    send: Option[Discriminated.PeerRequest[String, String]] = None
  )

  val Component = ScalaComponent.builder[Props]("Arcomage")
    .initialState(State())
    .renderBackend[Backend]
    .build

  class Backend($: BackendScope[Props, State]) {
    val generateOffer = for {
      props          <- $.props.task
      offerResultsL  <- props.messenger.makeOffer
      (offer, sendL) = offerResultsL
      _              <- $.modState(_.copy(myOffer = offer.string)).task

      send <- sendL
      _    <- $.modState(_.copy(send = send.some)).task
    } yield ()

    val connectToOffer = for {
      props <- $.props.task
      state <- $.state.task
      send  <- props.messenger.connect(Offer(state.hostOffer))
      _     <- $.modState(_.copy(send = send.some)).task
    } yield ()

    def setHostOffer(e: ReactEventFromInput) =
      Callback { e.persist() } >>
        $.modState(_.copy(hostOffer = e.target.value))

    def setMessage(e: ReactEventFromInput) =
      Callback { e.persist() } >>
        $.modState(_.copy(messageText = e.target.value))

    val sendMessage = for {
      state  <- $.state.task
      msg    = state.messageText
//      result <- state.send.fold(Task.pure(""))(_ apply msg)
//      _      = println(result)
    } yield ()

    def render(s: State, p: Props) = {
      import s._
      implicit val sc = p.scheduler

      <.div(
        button("Generate offer", generateOffer),
        <.input(
          ^.`type` := "text",
          ^.value := myOffer
        ),
        <.hr(),
        <.input(
          ^.`type` := "text",
          ^.value := hostOffer,
          ^.onChange ==> setHostOffer
        ),
        button("Connect", connectToOffer),
        <.hr(),
        <.input(
          ^.`type` := "text",
          ^.onChange ==> setMessage,
          ^.value := messageText
        ),
        button("Send", sendMessage),
        <.hr(),
        send.whenDefined(_ => <.span("Connection established"))
      )
    }
  }

  def button(label: String, onClick: Callback) =
    <.input(
      ^.`type` := "button",
      ^.value := label,
      ^.onClick --> onClick
    )
}
