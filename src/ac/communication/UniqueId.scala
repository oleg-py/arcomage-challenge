package ac.communication

import simulacrum.typeclass


@typeclass trait UniqueId[F[_]] {
  def generate: F[Int]
}
