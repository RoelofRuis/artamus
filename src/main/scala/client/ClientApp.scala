package client

import client.gui.Editor
import com.google.inject.Guice

object ClientApp extends App {

  val injector = Guice.createInjector(
    new ClientModule
  )

  import net.codingwell.scalaguice.InjectorExtensions._

  val editorThread = injector.instance[Editor].getThread
  editorThread.start()
  editorThread.join()

  midi.midiResources.closeAll() // TODO: refine the resource management further

}
