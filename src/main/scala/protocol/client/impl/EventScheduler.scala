package protocol.client.impl

import protocol.Event

trait EventScheduler {

  def schedule(event: Event): Unit

}
