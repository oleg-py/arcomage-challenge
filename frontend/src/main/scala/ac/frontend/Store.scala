package ac.frontend

import ac.frontend.states._
import cats.effect.{ContextShift, IO, Timer}
import com.olegpy.shironeko.StoreBase


//noinspection TypeAnnotation
object Store extends StoreBase[IO](Main.Instance) with StoreAlg[IO] {
  override object implicits extends implicits {
    implicit def timer: Timer[IO] = Main.timer
    implicit def contextShift: ContextShift[IO] = Main.contextShift
  }
}
