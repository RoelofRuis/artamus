package protocol.v2.api

import java.util.concurrent.{ArrayBlockingQueue, BlockingQueue}

import protocol.Event
import protocol.v2.api.ClientFactory.ClientConfig
import pubsub.{Callback, Dispatcher}

final class ClientFactory { // TODO: move to impl

  def createClient(config: ClientConfig, dispatcher: Dispatcher[Callback, Event]): ClientInterface2 = {

    val eventQueue: BlockingQueue[Event] = new ArrayBlockingQueue[Event](64)

    // create event thread
    // create client thread

    // create client? create thread factory?

    new Client2()
  }

}

object ClientFactory {

  final case class ClientConfig(
    port: Int
  )

}