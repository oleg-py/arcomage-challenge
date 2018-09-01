package ac.webapp.react

import ac.game.Resources
import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.vdom.html_<^._


case class ResourcesColumn (name: String, resources: Resources, income: Resources) {
  def /> = ResourcesColumn.Component(this)
}

object ResourcesColumn {
  val Component = ScalaComponent.builder[ResourcesColumn]("ResourcesColumn")
    .render_P { props =>
      <.div(
        ^.cls := "resources-column",
        <.div(
          ^.cls := "name",
          props.name
        ),
        ResourceDisplay(
          "Bricks",
          "bricks",
          props.resources.bricks,
          props.income.bricks
        )./>,
        ResourceDisplay(
          "Gems",
          "gems",
          props.resources.gems,
          props.income.gems
        )./>,
        ResourceDisplay(
          "Recruits",
          "recruits",
          props.resources.recruits,
          props.income.recruits
        )./>
      )
    }
    .build
}
