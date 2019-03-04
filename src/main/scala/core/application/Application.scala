package core.application

import com.google.inject.Inject
import core.components.{ApplicationRunner, Logger}

class Application @Inject() private (
  loggerRegistry: ServiceRegistry[Logger],
  resourceManager: ResourceManager,
  runner: ApplicationRunner
) {

  def run(): Unit = {
    // Enable terminal logging by default
    loggerRegistry.setActive("terminal")

    runner.run()
    resourceManager.closeAll()
  }

}

