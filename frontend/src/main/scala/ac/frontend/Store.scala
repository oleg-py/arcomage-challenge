package ac.frontend

import ac.frontend.states._
import cats.effect._
import com.olegpy.shironeko._
import monix.eval.Task


//noinspection TypeAnnotation
object Store extends StoreBase(Main.Instance)
  with StoreAlg[Task] with SlinkyIntegration[Task] with ImpureIntegration[Task]
{
  override object implicits extends implicits {
    implicit def timer: Timer[Task] = Task.timer
    implicit def contextShift: ContextShift[Task] = Task.contextShift
  }
}
