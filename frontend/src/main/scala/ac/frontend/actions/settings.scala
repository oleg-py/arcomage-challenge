package ac.frontend.actions
import ac.frontend.states.PersistentSettings.Repr
import ac.frontend.states.{ConditionsChoice, PersistentSettings}
import cats.effect.Sync

object settings {
  def persistUser[F[_]: Sync](name: String, email: String): F[Unit] = {
    PersistentSettings[F].modify(
      Repr.name.set(name) andThen Repr.email.set(email)
    )
  }

  def persistConditions[F[_]: Sync](f: ConditionsChoice => ConditionsChoice): F[Unit] = {
    PersistentSettings[F].modify(Repr.conditionsChoice.modify(f))
  }
}
