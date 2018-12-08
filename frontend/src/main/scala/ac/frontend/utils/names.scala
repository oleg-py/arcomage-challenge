package ac.frontend.utils

import scala.util.Random

import ac.frontend.actions.connect
import monix.eval.Coeval
import monix.execution.annotations.UnsafeBecauseImpure


object names {
  val forHost: Vector[String] =
    Vector("Tont", "Tair", "Oosi", "Logh", "Iunda", "Leeck", "Ecero",
         "Enn", "Uatha", "Rynk", "Yurno", "Chroir", "Undc", "Elery",
         "Itino", "Maph", "Thon", "Strach", "Okimo", "Aturi", "Rynr")

  val forGuest: Vector[String] =
    Vector("Shond", "Oumu", "Emr", "Rynch", "Nysch", "Meik", "Athl",
           "Iesse", "Acere", "Irn", "Ovoru", "Anrt", "Thal", "Naiq",
           "Clieph", "Narr", "Ackl", "Eenthe", "Igary", "Llyd", "Tonh")


  @UnsafeBecauseImpure
  def pick(): String = {
    val vec = if (connect.isGuest[Coeval].value()) forGuest else forHost
    vec(Random.nextInt(vec.length))
  }
}
