package ac.webapp

import boopickle.Default._
import monix.eval.{Task, TaskApp}

object Main extends TaskApp with Algebras {
  override def runc = Task.defer {
    Task.unit
  }
}
