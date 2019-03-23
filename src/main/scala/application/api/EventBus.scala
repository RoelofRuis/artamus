package application.api

import scala.reflect.runtime.universe._

import application.api.Events.EventMessage

trait EventBus {

  /**
    * Subscribes the given callback to receive a particular type of event message.
    */
  def subscribe[M <: EventMessage: TypeTag](f: M => Unit): Unit

}
