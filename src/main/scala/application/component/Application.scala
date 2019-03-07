package application.component

import application.ApplicationEntryPoint
import application.ports.{Driver, InputDevice, Logger, PlaybackDevice}
import javax.inject.Inject

private[application] class Application @Inject() private (
  loggerRegistry: ServiceRegistry[Logger],
  inputRegistry: ServiceRegistry[InputDevice],
  playbackRegistry: ServiceRegistry[PlaybackDevice],
  resourceManager: ResourceManager,
  driver: Driver
) extends ApplicationEntryPoint {

  def run(): Unit = {
    // TODO: better way to assign app defaults, for now enable practical default services
    inputRegistry.setActive("terminal")
    playbackRegistry.setActive("midi")

    driver.run()
    resourceManager.closeAll()
  }

}

