package server.io

import protocol.Event

class EventBus {

  private var recipients = Map[String, Event => Unit]()

  def publish[A <: Event](event: A): Unit = recipients.values.foreach(_(event))

  def subscribe(name: String, callback: Event => Unit): Unit = recipients += (name -> callback)

  def unsubscribe(name: String): Unit = recipients -= name

}
