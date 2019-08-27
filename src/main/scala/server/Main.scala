package server

import com.google.inject.Guice
import server.core.Server

object Main extends App {

  val injector = Guice.createInjector(
    new ServerModule
  )

  import net.codingwell.scalaguice.InjectorExtensions._
  val server = injector.instance[Server]

  server.run()

}
