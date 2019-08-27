package server

import server.interact.SocketCommandBus
import com.google.inject.Guice
import storage.StorageModule

object Server extends App {

  val injector = Guice.createInjector(
    new CoreModule,
    new StorageModule,
  )

  import net.codingwell.scalaguice.InjectorExtensions._
  val logger = injector.instance[Logger]
  val commandBus = injector.instance[SocketCommandBus]

  logger.debug("Starting server...")

  val commandBusThread = new Thread(() => commandBus.run())

  logger.debug(s"Running command bus ...")

  commandBusThread.start()

  logger.debug("Accepting commands on command bus...")

  commandBusThread.join()

  logger.debug("Shutting down, bye!")
}
