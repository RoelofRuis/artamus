package application.component

import application.ApplicationEntryPoint
import application.ports._
import javax.inject.Inject

private[application] class Application @Inject() private (
  recordingRegistry: ServiceRegistry[RecordingDevice],
  playbackRegistry: ServiceRegistry[PlaybackDevice],
  resourceManager: ResourceManager,
  driver: Driver
) extends ApplicationEntryPoint {

  def run(): Unit = {
    // TODO: better way to assign app defaults, for now enable practical default services
    recordingRegistry.onlyActivate("midi")
    playbackRegistry.onlyActivate("terminal")

    driver.run()
    resourceManager.closeAll()
  }

}

