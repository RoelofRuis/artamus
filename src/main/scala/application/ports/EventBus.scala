package application.ports

import application.channels.EventMessage

import scala.reflect.runtime.universe._

trait EventBus {

  /**
    * Subscribes the given callback to receive a particular type of event message.
    */
  def subscribe[M <: EventMessage: TypeTag](f: M => Unit): Unit

}
