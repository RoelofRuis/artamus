package interaction.terminal

import core.components.{ApplicationRunner, InputDevice}
import interaction.terminal.command._
import interaction.terminal.device.TerminalInputDevice
import net.codingwell.scalaguice.{ScalaMapBinder, ScalaModule, ScalaMultibinder}

class TerminalModule extends ScalaModule {

  override def configure(): Unit = {
    bind[ApplicationRunner].to[TerminalRunner]
    bind[Prompt].to[TerminalPrompt]

    val commands = ScalaMultibinder.newSetBinder[Command](binder)
    commands.addBinding.to[CreateIdeaCommand]
    commands.addBinding.to[ListIdeasCommand]
    commands.addBinding.to[PlaybackIdeaCommand]
    commands.addBinding.to[ListResourcesCommand]
    commands.addBinding.to[MidiCommand]
    commands.addBinding.to[SetLoggerCommand]
    commands.addBinding.to[SetInputDeviceCommand]
    commands.addBinding.to[SetPlaybackDeviceCommand]
    commands.addBinding.to[QuitCommand]

    ScalaMapBinder.newMapBinder[String, InputDevice](binder)
      .addBinding("terminal-inputDevice").to[TerminalInputDevice]
  }

}
