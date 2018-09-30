package ac.frontend

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport


@JSImport("resources/offsets.csv", JSImport.Namespace)
@js.native
object CardData extends js.Array[CardDataElement]

trait CardDataElement extends js.Object {
  def name_en: String
  def offset_x: Int
  def offset_y: Int
}