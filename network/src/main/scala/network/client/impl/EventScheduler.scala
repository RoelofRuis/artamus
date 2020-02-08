package network.client.impl

import network.EventResponseMessage

import scala.util.Try

private[client] trait EventScheduler {

  def schedule(eventResponseMessage: EventResponseMessage[_]): Try[Unit]

}
