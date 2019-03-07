package interaction.terminal

import application.components.{ApplicationRunner, InputDevice, Logger, PlaybackDevice}
import interaction.terminal.command.ConfigureCommand.ConfigDescription
import interaction.terminal.command._
import interaction.terminal.device.{TerminalInputDevice, TerminalLogger}
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
    commands.addBinding.to[ConfigureCommand[Logger]]
    commands.addBinding.to[ConfigureCommand[InputDevice]]
    commands.addBinding.to[ConfigureCommand[PlaybackDevice]]
    commands.addBinding.to[QuitCommand]

    bind[ConfigDescription[Logger]].toInstance(ConfigDescription("logger", "conf-logger", "Configure the system logger"))
    bind[ConfigDescription[InputDevice]].toInstance(ConfigDescription("input device", "conf-input", "Configure the system input device"))
    bind[ConfigDescription[PlaybackDevice]].toInstance(ConfigDescription("playback device", "conf-playback", "Configure the system playback device"))

    ScalaMapBinder.newMapBinder[String, InputDevice](binder)
      .addBinding("terminal").to[TerminalInputDevice]

    ScalaMapBinder.newMapBinder[String, Logger](binder)
      .addBinding("terminal").to[TerminalLogger]
  }

}
