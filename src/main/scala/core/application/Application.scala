package core

import com.google.inject.Inject
import core.components.ApplicationRunner

class Application @Inject() private (resourceCloser: ResourceManager, runner: ApplicationRunner) {

  def run(): Unit = {
    runner.run()
    resourceCloser.closeAll()
  }

}

