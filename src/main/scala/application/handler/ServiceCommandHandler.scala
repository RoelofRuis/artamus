package application.handler

import application.command.Command
import application.command.ServiceCommand._
import application.component.ServiceRegistry
import javax.inject.Inject

import scala.util.{Success, Try}

/**
  * @deprecated No longer used, but kept for now to show the functionality
  *             Should be replaced by different contains passed to plugins and management by service name instead of type.
  */
private[application] class ServiceCommandHandler[A] @Inject() (registry: ServiceRegistry[A]) extends CommandHandler {

  override def handle[Res]: PartialFunction[Command[Res], Try[Res]] = {
    case _: GetAll[A] => Success(registry.getRegistered.map(name => (name, registry.isActive(name))))
    case _: HasActive[A] => Success(registry.hasActive)
    case _: DeactivateAll[A] => Success(registry.deactivateAll())
    case c: Toggle[A] => Success(toggle(c.optionName))
    case _: AllowsMultiple[A] => Success(registry.allowsMultiple)
  }

  def toggle(string: String): Boolean = {
    if (registry.allowsMultiple) {
      if (registry.isActive(string)) registry.deactivateOne(string)
      else registry.alsoActivate(string)
    } else {
      if (registry.isActive(string)) registry.deactivateAll()
      else registry.onlyActivate(string)
    }
  }

}
