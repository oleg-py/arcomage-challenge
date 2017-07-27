package ac.webapp.react

import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.vdom.html_<^._


case class ResourceDisplay (label: String, cls: String, resource: Int, income: Int) {
  def /> = ResourceDisplay.Component(this)
}

object ResourceDisplay {
  val Component = ScalaComponent.builder[ResourceDisplay]("ResourceDisplay")
    .render_P { props =>
      <.div(
        ^.cls := s"resource-display ${props.cls}",
        <.div(
          ^.cls := "income-box",
          <.div(
            ^.cls := "text",
            props.income.toString
          )
        ),
        <.div(
          ^.cls := "current-box",
          <.div(
            ^.cls := "label",
            props.label
          ),
          <.div(
            ^.cls := "value",
            props.resource.toString
          )
        )
      )
    }
    .build
}
