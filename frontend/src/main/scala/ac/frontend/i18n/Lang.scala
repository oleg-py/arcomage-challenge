package ac.frontend.i18n


sealed trait Lang

object Lang {
  case object En extends Lang
  case object Ru extends Lang
}