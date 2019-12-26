package protocol.v2.client

import protocol.v2.Event2
import protocol.v2.client.impl.{Client2, EventThread}
import pubsub.{Callback, Dispatcher}

package object api {

  def createClient(config: ClientConfig, dispatcher: Dispatcher[Callback, Event2]): ClientInterface2 = {
    val eventScheduler = new EventThread(dispatcher)
    eventScheduler.setDaemon(true)
    eventScheduler.start()

    new Client2(config, eventScheduler)
  }

}
