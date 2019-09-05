package client

import com.google.inject.Guice
import protocol.client.ClientInterface
import server.control.Disconnect

object ClientApp extends App {

  val injector = Guice.createInjector(
    new ClientModule
  )

  import net.codingwell.scalaguice.InjectorExtensions._
  val client = injector.instance[ClientInterface]

  client.sendControl(Disconnect(true))

  client.close()

  midi.close() // UGLY AS FUCK!

}

