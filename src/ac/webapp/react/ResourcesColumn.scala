package ac.webapp.react

import ac.game.Resources
import ReactSyntax._


case class ResourcesColumn (name: String, resources: Resources, income: Resources) {
  def /> = ResourcesColumn.Component(this)
}

object ResourcesColumn {
  val Component = ScalaComponent.builder[ResourcesColumn]("ResourcesColumn")
    .render_P { props =>
      div(`class` := "resources-column")(
        div(`class` := "name", props.name),
        ResourceDisplay("Bricks",   "bricks"  , props.resources.bricks, props.income.bricks)./>,
        ResourceDisplay("Gems",     "gems"    , props.resources.gems  , props.income.gems)./>,
        ResourceDisplay("Recruits", "recruits", props.resources.recruits, props.income.recruits)./>
      )
    }
    .build
}
