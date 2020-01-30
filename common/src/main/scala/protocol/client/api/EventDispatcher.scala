package protocol.client.api

import protocol.Event

trait EventDispatcher {

  def dispatch(event: Event): Unit

}
