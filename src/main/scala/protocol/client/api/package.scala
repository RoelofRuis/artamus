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

}
