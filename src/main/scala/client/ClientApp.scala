package client

import com.google.inject.Guice

object ClientApp extends App {

  val injector = Guice.createInjector(
    new ClientModule
  )

  import net.codingwell.scalaguice.InjectorExtensions._
  val app = injector.instance[Bootstrapper]

  app.run()

  midi.resourceManager.closeAll() // TODO: refine the resource management further

}

