package ac.frontend.utils

// Literally just to provide a better syntax for pre-loading CSS from SJS,
// where an `object` needs to be referenced once to be included by bundler
object bundle {
  @noinline final def apply(a: Any*): Unit = mouse.ignore(a)
}
