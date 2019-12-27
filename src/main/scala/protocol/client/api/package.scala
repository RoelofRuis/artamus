package protocol.client

import protocol.Event
import protocol.client.impl.{Client2, EventThread}
import pubsub.{Callback, Dispatcher}

package object api {

  def createClient(config: ClientConfig, dispatcher: Dispatcher[Callback, Event]): ClientInterface2 = {
    val eventScheduler = new EventThread(dispatcher)
    eventScheduler.setDaemon(true)
    eventScheduler.start()

    new Client2(config, eventScheduler)
  }

}
