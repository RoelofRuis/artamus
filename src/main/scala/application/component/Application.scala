package application.component

import application.ApplicationEntryPoint
import application.ports._
import javax.inject.Inject

import scala.collection.immutable

private[application] class Application @Inject() private (
  recordingRegistry: ServiceRegistry[RecordingDevice],
  resourceManager: ResourceManager,
  messageBus: SynchronizedMessageBus,
  eventBus: CoreEventBus,
  drivers: immutable.Map[String, Driver],
  logger: Logger
) extends ApplicationEntryPoint {

  def run(): Unit = {
    // TODO: better way to assign app defaults, for now enable practical default services
    recordingRegistry.onlyActivate("midi")

    logger.debug("Starting drivers...")
    val driverThreads: Map[String, Thread] = drivers.map {
      case (name, driver) => name -> new Thread(() => driver.run(messageBus, eventBus))
    }

    if (driverThreads.isEmpty) {
      logger.debug("No drivers registered...")
    }

    driverThreads.foreach { case (name, thread) =>
      logger.debug(s"Starting driver [$name]")
      thread.start()
    }

    logger.debug("Accepting messages on message bus...")

    while (messageBus.handle()) {}

    driverThreads.foreach { case (name, thread) =>
      logger.debug(s"Waiting 5s for thread [$name] to join...")
      thread.join(5000)
    }

    logger.debug("Closing resources...")
    resourceManager.closeAll()
  }

}

