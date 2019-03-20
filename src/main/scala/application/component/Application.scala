package application.component

import application.ApplicationEntryPoint
import application.ports._
import javax.inject.Inject

import scala.collection.immutable

private[application] class Application @Inject() private (
  recordingRegistry: ServiceRegistry[RecordingDevice],
  playbackRegistry: ServiceRegistry[PlaybackDevice],
  resourceManager: ResourceManager,
  messageBus: SynchronizedMessageBus,
  drivers: immutable.Map[String, Driver],
  logger: Logger
) extends ApplicationEntryPoint {

  def run(): Unit = {
    // TODO: better way to assign app defaults, for now enable practical default services
    recordingRegistry.onlyActivate("midi")
    playbackRegistry.onlyActivate("terminal")

    logger.debug("Starting drivers...")
    val driverThreads: Map[String, Thread] = drivers.map {
      case (name, driver) => name -> new Thread(() => driver.run(messageBus))
    }

    if (driverThreads.isEmpty) {
      logger.debug("No drivers registered, shutting down...")
    }

    driverThreads.foreach { case (name, thread) =>
      logger.debug(s"Starting driver [$name]")
      thread.start()
    }

    logger.debug("Starting application...")

    while (messageBus.handle())

    logger.debug(s"Threads: $driverThreads")

    driverThreads.foreach { case (name, thread) =>
      logger.debug(s"Waiting 5s for thread [$name] to join...")
      thread.join(5000)
    }

    logger.debug("Closing resources...")
    resourceManager.closeAll()
  }

}

