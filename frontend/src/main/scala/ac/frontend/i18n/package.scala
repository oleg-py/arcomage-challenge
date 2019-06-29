package ac.frontend

import ac.game.cards.dsl.DescribeInterpreter
import slinky.core.facade.{Hooks, React, ReactElement}


package object i18n {
  private[i18n] val LangContext = React.createContext(null: Lang)
  def withLang(f: Lang => ReactElement): ReactElement = LangContext.Consumer(f)

  val genericCardDescription = Tr(DescribeInterpreter.en, DescribeInterpreter.ru)
}
