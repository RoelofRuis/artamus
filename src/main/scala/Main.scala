import io.cmd.CmdModule
import com.google.inject.Guice
import core.CoreModule
import core.app.AppRunner

object Main extends App {

  val injector = Guice.createInjector(
    new CoreModule,
    new CmdModule
  )

  import net.codingwell.scalaguice.InjectorExtensions._
  val runner = injector.instance[AppRunner]

  runner.run()

}
