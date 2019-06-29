package ac.frontend

import slinky.core.facade.{Hooks, React, ReactElement}


package object i18n {
  private[i18n] val LangContext = React.createContext(null: Lang)
  def getLang(): Lang = Hooks.useContext(LangContext)
  def withLang(f: Lang => ReactElement): ReactElement = LangContext.Consumer(f)
}
