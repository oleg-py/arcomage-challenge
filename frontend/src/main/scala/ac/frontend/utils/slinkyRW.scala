package ac.frontend.utils

import scala.scalajs.js

import eu.timepit.refined.api.Refined
import slinky.core.{StateReaderProvider, StateWriterProvider}
import slinky.readwrite.{Reader, Writer}

object slinkyRW {
  object stfu {
    implicit val stateReaderProvider: StateReaderProvider = Reader.fallback[Any].asInstanceOf[js.Any].jsCast
    implicit val stateWriterProvider: StateWriterProvider = Writer.fallback[Any].asInstanceOf[js.Any].jsCast
  }
}
