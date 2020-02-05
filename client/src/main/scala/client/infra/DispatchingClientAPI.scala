package client.infra

import com.typesafe.scalalogging.LazyLogging
import domain.interact.Event
import javax.inject.Inject
import network.client.api.ClientAPI
import pubsub.{Callback, Dispatcher}

class DispatchingClientAPI @Inject() (
  dispatcher: Dispatcher[Callback, Event]
) extends ClientAPI[Event] with LazyLogging {

  override def receivedEvent(event: Event): Unit = dispatcher.handle(Callback(event))

  override def connectingStarted(): Unit = {
    logger.info("Connecting started")
  }

  override def connectingFailed(cause: Throwable): Unit = {
    logger.warn("Connecting failed", cause)
  }

  override def connectionEstablished(): Unit = {
    logger.info("Connection established")
  }

  override def connectionLost(cause: Throwable): Unit = {
    logger.warn("Connection lost: {}", cause)
  }
}
