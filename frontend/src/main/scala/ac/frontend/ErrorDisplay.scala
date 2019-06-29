package ac.frontend

import slinky.core.facade.ReactElement
import slinky.web.html._

object ErrorDisplay extends Store.Container {
  type State = Option[String]
  type Props = ReactElement

  def subscribe[F[_]: Subscribe]: fs2.Stream[F, State] =
    getAlgebra.error.discrete

  def render[F[_]: Render](state: State, props: Props): ReactElement =
    state.fold(props)(div(className := "error-dialog box")(_))
}
