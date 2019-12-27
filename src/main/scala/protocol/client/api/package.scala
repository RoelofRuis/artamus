package protocol.client

import protocol.Event
import protocol.client.impl.{Client, EventThread}
import pubsub.{Callback, Dispatcher}

package object api {

  def createClient(config: ClientConfig, dispatcher: Dispatcher[Callback, Event]): ClientInterface = {
    val eventScheduler = new EventThread(dispatcher)
    eventScheduler.setDaemon(true)
    eventScheduler.start()

    new Client(config, eventScheduler)
  }

  sealed trait ConnectionEvent extends Event
  final case object ConnectingStarted extends ConnectionEvent
  final case object ConnectingFailed extends ConnectionEvent
  final case object ConnectionMade extends ConnectionEvent
  final case object ConnectionLost extends ConnectionEvent

}
