package protocol.v2.client.impl

import protocol.v2.Event2

trait EventScheduler {

  def schedule(event: Event2): Unit

}
