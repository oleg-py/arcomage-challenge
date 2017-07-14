package ac.communication

trait Collectable[O[_], M[_]] {
  def collectFirstM[A, B](o: O[A])(f: PartialFunction[A, B]): M[B]
  def collectM_[A, B]   (o: O[A])(f: PartialFunction[A, M[B]]): M[Unit]
}
