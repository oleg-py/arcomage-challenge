package ac.frontend

import ac.frontend.states.StoreAlg
import com.olegpy.shironeko.SlinkyConnector
import fs2.Pure
import monix.eval.Task
import slinky.core.facade.ReactElement


object Store extends SlinkyConnector[Task, StoreAlg[Task]] {
  trait ContainerNoState extends Container {
    def render(props: Props)(implicit F: StoreAlg[Task]): ReactElement

    type State = Unit
    final override def subscribe(implicit F: StoreAlg[Task]): fs2.Stream[Pure, Unit] =
      fs2.Stream(())

    final override def render(state: Unit, props: Props)(implicit F: StoreAlg[Task]): ReactElement =
      render(props)
  }
}
