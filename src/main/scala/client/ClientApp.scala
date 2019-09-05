package client

import com.google.inject.Guice
import protocol.client.MessageBus
import server.control.Disconnect

object ClientApp extends App {

  val injector = Guice.createInjector(
    new ClientModule
  )

  import net.codingwell.scalaguice.InjectorExtensions._
  val messageBus = injector.instance[MessageBus]



  messageBus.sendControl(Disconnect(true))

}

