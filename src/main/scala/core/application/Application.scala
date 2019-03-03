package core.application

import com.google.inject.Inject
import core.components.ApplicationRunner

class Application @Inject() private (
  resourceManager: ResourceManager,
  runner: ApplicationRunner
) {

  def run(): Unit = {
    runner.run()
    resourceManager.closeAll()
  }

}

