package ac.frontend.i18n

import ac.frontend.Store
import slinky.core.facade.ReactElement


object LangSupport extends Store.Container {
  override type State = Lang
  override type Props = ReactElement

  override def subscribe[F[_]: Subscribe]: fs2.Stream[F, Lang] =
    getAlgebra.locale.discrete

  override def render[F[_]: Render](state: Lang, props: ReactElement): ReactElement =
    LangContext.Provider(state)(props)
}
