package ac.frontend

import ac.frontend.states.StoreAlg
import com.olegpy.shironeko.SlinkyConnector
import monix.eval.Task


object Store extends SlinkyConnector[Task, StoreAlg[Task]]
