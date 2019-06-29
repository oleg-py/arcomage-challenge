package ac.frontend.i18n

import cats.Traverse
import slinky.core.facade.ReactElement


case class Tr[A](en: A, ru: A) {
  def in(l: Lang): A = l match {
    case Lang.En => en
    case Lang.Ru => ru
  }
}

object Tr {
  implicit def trReactElement[A](tr: Tr[A])(implicit view: A => ReactElement, lang: Lang): ReactElement =
    view(tr in lang)

  implicit val trTraverse: Traverse[Tr] = cats.derived.semi.traverse[Tr]
}
