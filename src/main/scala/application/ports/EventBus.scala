package application.ports

import scala.reflect.runtime.universe._

trait EventBus {

  def subscribe[A: TypeTag](sub: A => Unit): Unit

}
