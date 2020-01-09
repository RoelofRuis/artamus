package client

import client.gui.Editor
import com.google.inject.Guice

object ClientApp extends App {

  val injector = Guice.createInjector(
    new ClientModule
  )

  import net.codingwell.scalaguice.InjectorExtensions._

  val editorThread = injector.instance[Editor]
  editorThread.start()
  editorThread.join()

}
