package application.component

import application.BootstrapperInterface
import com.google.inject.Inject
import application.ports.{ApplicationRunner, InputDevice, Logger, PlaybackDevice}

private[application] class Bootstrapper @Inject() private (
  loggerRegistry: ServiceRegistry[Logger],
  inputRegistry: ServiceRegistry[InputDevice],
  playbackRegistry: ServiceRegistry[PlaybackDevice],
  resourceManager: ResourceManager,
  runner: ApplicationRunner
) extends BootstrapperInterface {

  def run(): Unit = {
    // TODO: better way to assign app defaults, for now enable practical default services
    inputRegistry.setActive("terminal")
    playbackRegistry.setActive("midi")

    runner.run()
    resourceManager.closeAll()
  }

}

