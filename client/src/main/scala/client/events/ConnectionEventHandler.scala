package client.events

import com.google.inject.Inject
import com.typesafe.scalalogging.LazyLogging
import network.client.api._
import pubsub.{Callback, Dispatcher}

import scala.util.Success

class ConnectionEventHandler @Inject() (
  dispatcher: Dispatcher[Callback, ConnectionEvent],
) extends LazyLogging {

  dispatcher.subscribe[ConnectingStarted.type] { _ =>
    logger.info("Connecting started")
    Success(())
  }

  dispatcher.subscribe[ConnectingFailed.type] { _ =>
    logger.warn("Connecting failed")
    Success(())
  }

  dispatcher.subscribe[ConnectionMade.type] { _ =>
    logger.info("Connection made")
    Success(())
  }

  dispatcher.subscribe[ConnectionLost.type] { _ =>
    logger.warn("Connection lost")
    Success(())
  }
}
