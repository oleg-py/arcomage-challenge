package ac.frontend
import ac.frontend.states.StoreAlg
import monix.eval.Task
import slinky.core.facade.ReactElement
import slinky.web.html._

object ErrorDisplay extends Store.Container {
  type State = Option[String]
  type Props = ReactElement

  def subscribe(implicit F: StoreAlg[Task]): fs2.Stream[Task, State] =
    F.error.listen

  def render(state: State, props: Props)(implicit F: StoreAlg[Task]): ReactElement =
    state.fold(props)(div(className := "error-dialog box")(_))
}
