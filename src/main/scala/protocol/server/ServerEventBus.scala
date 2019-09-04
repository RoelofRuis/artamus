package protocol.server

import protocol.Event

private[protocol] class ServerEventBus private[protocol] (eventRegistry: ServerEventRegistry) extends EventBus {

  def publishEvent[A <: Event](event: A): Unit = eventRegistry.publish(event)

}
