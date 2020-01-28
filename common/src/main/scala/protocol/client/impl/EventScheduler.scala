package protocol.client.impl

import protocol.Event

private[client] trait EventScheduler {

  def schedule(event: Event): Unit

}
