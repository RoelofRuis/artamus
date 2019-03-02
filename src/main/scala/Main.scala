import com.google.inject.Guice
import core.CoreModule
import core.components.AppRunner
import interaction.terminal.TerminalModule
import logging.LoggingModule
import storage.StorageModule

object Main extends App {

  val injector = Guice.createInjector(
    new CoreModule,
    new TerminalModule,
    new StorageModule,
    new LoggingModule,
  )

  import net.codingwell.scalaguice.InjectorExtensions._
  val runner = injector.instance[AppRunner]

  runner.run()

}
