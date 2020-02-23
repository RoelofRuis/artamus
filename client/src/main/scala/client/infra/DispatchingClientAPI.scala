package client.infra

import com.typesafe.scalalogging.LazyLogging
import domain.interact.Event
import javax.inject.Inject
import network.Exceptions.ResponseException
import network.client.api.ClientAPI
import pubsub.Dispatcher

class DispatchingClientAPI @Inject() (
  dispatcher: Dispatcher[Callback, Event]
) extends ClientAPI[Event] with LazyLogging {

  override def handleEvent(event: Event): Unit = dispatcher.handle(Callback(event))

  override def receivedInvalidEvent(cause: Throwable): Unit = {
    logger.warn("Received invalid event", cause)
  }

  override def receivedUnexpectedResponse(response: Either[ResponseException, Any]): Unit = {
    logger.warn(s"Received unexpected response [$response]")
  }

  override def connectionEstablished(): Unit = {
    logger.info("Connection established")
  }

  override def connectionLost(cause: Throwable): Unit = {
    logger.warn("Connection lost: {}", cause)
  }



}
