package ac.webapp

import monix.eval.{Task, TaskApp}

object Main extends TaskApp with Algebras {
  override def runc = Task.defer {
    Task.unit
  }
}
