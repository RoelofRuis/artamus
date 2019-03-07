import com.google.inject.Guice
import core.CoreModule
import core.components.BootstrapperInterface
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
  val runner = injector.instance[BootstrapperInterface]

  runner.run()
}
