package application.component

import application.ApplicationEntryPoint
import com.google.inject.Inject
import application.ports.{Driver, InputDevice, Logger, PlaybackDevice}

private[application] class Application @Inject() private (
  loggerRegistry: ServiceRegistry[Logger],
  inputRegistry: ServiceRegistry[InputDevice],
  playbackRegistry: ServiceRegistry[PlaybackDevice],
  resourceManager: ResourceManager,
  runner: Driver
) extends ApplicationEntryPoint {

  def run(): Unit = {
    // TODO: better way to assign app defaults, for now enable practical default services
    inputRegistry.setActive("terminal")
    playbackRegistry.setActive("midi")

    runner.run()
    resourceManager.closeAll()
  }

}

