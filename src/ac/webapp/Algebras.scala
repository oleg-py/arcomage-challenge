package ac.webapp

import scala.util.Random

import ac.game.Randomizer
import monix.eval.Task

trait Algebras {
  implicit object ImpureRandomizer extends Randomizer[Task] {
    override def shuffles[A](v: TraversableOnce[A]): Task[Stream[A]] =
      Task.eval { Stream.continually(Random.shuffle(v)).flatten }
  }
}
