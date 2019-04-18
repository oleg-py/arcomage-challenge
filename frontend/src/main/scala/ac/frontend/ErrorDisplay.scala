package ac.frontend
import ac.frontend.states.StoreAlg
import cats.effect.Concurrent
import com.olegpy.shironeko.interop.Exec
import slinky.core.facade.ReactElement
import slinky.web.html._

object ErrorDisplay extends Store.Container {
  type State = Option[String]
  type Props = ReactElement

  def subscribe[F[_]: Concurrent](implicit F: StoreAlg[F]): fs2.Stream[F, State] =
    F.error.discrete

  def render[F[_]: Concurrent: StoreAlg: Exec](state: State, props: Props): ReactElement =
    state.fold(props)(div(className := "error-dialog box")(_))
}
