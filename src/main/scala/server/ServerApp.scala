package server

import com.google.inject.Guice

object ServerApp extends App {

  val injector = Guice.createInjector(
    new ServerModule
  )

  import net.codingwell.scalaguice.InjectorExtensions._
  val app = injector.instance[Bootstrapper]

  app.run()

}
