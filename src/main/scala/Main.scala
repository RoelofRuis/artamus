import com.google.inject.Guice
import application.{ApplicationEntryPoint, CoreModule}
import interaction.midi.MidiModule
import interaction.terminal.TerminalModule
import storage.StorageModule

object Main extends App {

  val injector = Guice.createInjector(
    new CoreModule,
    new TerminalModule,
    new MidiModule,
    new StorageModule
  )

  import net.codingwell.scalaguice.InjectorExtensions._
  val application = injector.instance[ApplicationEntryPoint]

  application.run()
}
