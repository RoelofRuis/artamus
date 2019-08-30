package protocol.server

import protocol.Event

class ServerEventRegistry {

  private var recipient: Option[Event => Unit] = None

  def publish[A <: Event](event: A): Unit = recipient.foreach(_(event))

  def subscribe(callback: Event => Unit): Unit = recipient = Some(callback)

  def unsubscribe(): Unit = recipient = None

}
