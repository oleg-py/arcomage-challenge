package ac.webapp.react

import scalacss.ScalaCssReactImplicits

import japgolly.scalajs.react.ReactEventTypes
import japgolly.scalajs.{react => sr}
import sr.vdom.{HtmlAttrAndStyles, HtmlTags, PackageBase}


object ReactSyntax extends PackageBase
  with HtmlTags
  with HtmlAttrAndStyles
  with ReactEventTypes
  with ScalaCssReactImplicits
{
  val ScalaComponent = sr.ScalaComponent
  type BackendScope[P, S] = sr.BackendScope[P, S]
  type Callback = sr.Callback
  val Callback = sr.Callback
}
