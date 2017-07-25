package ac.webapp.react

import ReactSyntax._


case class ResourceDisplay (label: String, `class`: String, resource: Int, income: Int) {
  def /> = ResourceDisplay.Component(this)
}

object ResourceDisplay {
  val Component = ScalaComponent.builder[ResourceDisplay]("ResourceDisplay")
    .render_P { props =>
      div(className := s"resource-display ${props.`class`}")(
        div(`class` := "income-box")(
          div(`class` := "income-text", props.income.toString)
        ),
        div(`class` := "current-box")(
          div(`class` := "current-label", props.label),
          div(`class` := "current-value", props.resource.toString)
        )
      )
    }
    .build
}
