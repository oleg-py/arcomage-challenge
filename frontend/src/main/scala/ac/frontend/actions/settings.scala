package ac.frontend.actions
import ac.frontend.states.PersistentSettings.Repr
import ac.frontend.states.{ConditionsChoice, PersistentSettings, StoreAlg}

object settings {
  def persistUser[F[_]](name: String, email: String)(implicit Store: StoreAlg[F]): F[Unit] = {
    import Store.implicits._
    PersistentSettings[F].modify(
      Repr.name.set(name) andThen Repr.email.set(email)
    )
  }

  def persistConditions[F[_]](f: ConditionsChoice => ConditionsChoice)(implicit Store: StoreAlg[F]): F[Unit] = {
    import Store.implicits._
    PersistentSettings[F].modify(Repr.conditionsChoice.modify(f))
  }
}
