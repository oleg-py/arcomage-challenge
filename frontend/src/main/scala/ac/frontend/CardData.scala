package ac.frontend

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

import ac.frontend.i18n.Tr


@JSImport("resources/offsets.csv", JSImport.Namespace)
@js.native
object CardData extends js.Array[CardDataElement]

trait CardDataElement extends js.Object {
  def name_en: String
  def name_ru: String
  def offset_x: Int
  def offset_y: Int
  def description_en: js.UndefOr[String]
  def description_ru: js.UndefOr[String]
}

object CardDataElement {
  implicit class RichDataElement(private val self: CardDataElement) extends AnyVal {
    def localizedName: Tr[String] = Tr(self.name_en, self.name_ru)
    def customDescription: Tr[js.UndefOr[String]] = Tr(self.description_en, self.description_ru)
    def spriteOffsets: (Int, Int) = (self.offset_x, self.offset_y)
  }
}