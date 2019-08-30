package server

import com.google.inject.Guice

object Main extends App {

  val injector = Guice.createInjector(
    new ServerModule
  )

  import net.codingwell.scalaguice.InjectorExtensions._
  val server = injector.instance[Bootstrapper]

  server.run()

}
