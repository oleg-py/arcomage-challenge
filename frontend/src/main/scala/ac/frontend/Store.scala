package ac.frontend

import ac.frontend.states.StoreAlg
import com.olegpy.shironeko.SlinkyShironeko
import monix.eval.Task


object Store extends SlinkyShironeko[Task, StoreAlg[Task]]
