package ac.frontend

import ac.frontend.states._
import cats.effect._
import com.olegpy.shironeko._


//noinspection TypeAnnotation
object Store extends StoreBase[IO](Main.Instance)
  with StoreAlg[IO] with SlinkyIntegration[IO] with ImpureIntegration[IO]
{
  override object implicits extends implicits {
    implicit def timer: Timer[IO] = Main.timer
    implicit def contextShift: ContextShift[IO] = Main.contextShift
  }
}
