import com.google.inject.Guice
import core.CoreModule
import core.components.AppRunner
import interaction.terminal.TerminalModule
import storage.StorageModule

object Main extends App {

  val injector = Guice.createInjector(
    new CoreModule,
    new TerminalModule,
    new StorageModule
  )

  import net.codingwell.scalaguice.InjectorExtensions._
  val runner = injector.instance[AppRunner]

  runner.run()

}
