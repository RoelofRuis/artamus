package protocol

import java.util.concurrent.{ArrayBlockingQueue, BlockingQueue}

import com.typesafe.scalalogging.LazyLogging
import pubsub.{Callback, DispatchThread, Dispatcher}
import resource.Resource
import transport.client.ClientThread

class DefaultClient(
  client: Resource[ClientThread],
  eventDispatcher: Dispatcher[Callback, Event],
) extends ClientInterface with LazyLogging {

  private val eventQueue: BlockingQueue[Event] = new ArrayBlockingQueue[Event](64)

  private val eventThread: Thread = new DispatchThread[Event](eventQueue, eventDispatcher)
  private val clientThread: ClientMessagingThread = new ClientMessagingThread(eventQueue, client)

  override def open(): Unit = {
    eventThread.start()
    clientThread.start()
  }

  override def sendCommand[A <: protocol.Command](message: A): Option[protocol.Command#Res] = {
    clientThread.sendRequest[CommandRequest, A#Res](CommandRequest(message))
  }

  override def sendQuery[A <: protocol.Query](message: A): Option[A#Res] = {
    clientThread.sendRequest[QueryRequest, A#Res](QueryRequest(message))
  }

  override def close(): Unit = {
    eventThread.interrupt()
    clientThread.interrupt()
  }

}

object DefaultClient {

  def apply(port: Int, dispatcher: Dispatcher[Callback, Event]): ClientInterface = {
    new DefaultClient(Resource.wrapUnsafe(ClientThread(port), _.interrupt()), dispatcher)
  }

}