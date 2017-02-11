package ac.model

import scala.language.postfixOps

package object cards {
  val deck: Vector[Card] = (red cards) ++ (blue cards) ++ (green cards)
}
