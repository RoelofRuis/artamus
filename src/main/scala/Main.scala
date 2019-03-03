import com.google.inject.Guice
import core.{Application, CoreModule}
import interaction.midi.MidiModule
import interaction.terminal.TerminalModule
import logging.LoggingModule
import storage.StorageModule

object Main extends App {

  val injector = Guice.createInjector(
    new CoreModule,
    new TerminalModule,
    new MidiModule,
    new StorageModule,
    new LoggingModule,
  )

  import net.codingwell.scalaguice.InjectorExtensions._
  val runner = injector.instance[Application]

  runner.run()

}
