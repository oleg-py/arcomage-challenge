package ac.frontend.i18n

import ac.frontend.Store
import slinky.core.facade.ReactElement
import slinky.web.html._


object LocaleSelector extends Store.ContainerNoProps {
  override type State = Lang

  override def render[F[_]: Render](state: Lang): ReactElement =
    div(className := "language-selector")(
      Seq(Lang.En, Lang.Ru).map { lang =>
        val cls = s"language ${ if (lang == state) "active" else "" }"
        div(
          className := cls,
          key := lang.toString,
          onClick := { () => exec(getAlgebra.locale.set(lang)) }
        )(lang.toString)
      }
    )

  override def subscribe[F[_]: Subscribe]: fs2.Stream[F, Lang] = getAlgebra.locale.discrete
}
