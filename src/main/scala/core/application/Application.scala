package core.application

import com.google.inject.Inject
import core.components.{ApplicationRunner, InputDevice, Logger, PlaybackDevice}

class Application @Inject() private (
  loggerRegistry: ServiceRegistry[Logger],
  inputRegistry: ServiceRegistry[InputDevice],
  playbackRegistry: ServiceRegistry[PlaybackDevice],
  resourceManager: ResourceManager,
  runner: ApplicationRunner
) {

  def run(): Unit = {
    // TODO: better way to assign app defaults, for now enable practical default services
    inputRegistry.setActive("terminal")
    playbackRegistry.setActive("midi")

    runner.run()
    resourceManager.closeAll()
  }

}

