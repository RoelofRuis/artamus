package application.component

import application.ApplicationEntryPoint
import application.ports._
import javax.inject.Inject

import scala.collection.immutable

private[application] class Application @Inject() private (
  messageBus: SynchronizedMessageBus,
  eventBus: DomainEventBus,
  drivers: immutable.Map[String, Driver],
  logger: Logger
) extends ApplicationEntryPoint {

  def run(): Unit = {
    logger.debug("Starting drivers...")

    val driverThreads: Map[String, (Driver, Thread)] = drivers.map {
      case (name, driver) => name -> (driver, new Thread(() => driver.run(messageBus, eventBus)))
    }

    if (driverThreads.isEmpty) {
      logger.debug("No drivers registered...")
    }

    driverThreads.foreach { case (name, (_, thread)) =>
      logger.debug(s"Starting driver thread [$name]")
      thread.start()
    }

    logger.debug("Accepting messages on message bus...")

    while (messageBus.handle()) {}

    driverThreads.foreach { case (name, (driver, thread)) =>
      logger.debug(s"Closing driver [$name]")
      driver.close()
      logger.debug(s"Waiting 5s for thread [$name] to join...")
      thread.join(5000)
    }
  }

}

