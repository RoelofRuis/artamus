package interaction.terminal

import application.ports._
import interaction.terminal.command.ConfigureServiceCommand.ConfigDescription
import interaction.terminal.command._
import interaction.terminal.device.{TerminalInputDevice, TerminalLogger, TerminalPlaybackDevice}
import net.codingwell.scalaguice.{ScalaMapBinder, ScalaModule, ScalaMultibinder}

class TerminalModule extends ScalaModule {

  override def configure(): Unit = {
    bind[Driver].to[TerminalDriver]
    bind[Prompt].to[TerminalPrompt]

    val commands = ScalaMultibinder.newSetBinder[Command](binder)
    commands.addBinding.to[CreateIdeaCommand]
    commands.addBinding.to[ListTracksCommand]
    commands.addBinding.to[PlayTrackCommand]
    commands.addBinding.to[StartRecordingCommand]
    commands.addBinding.to[StoreRecordingCommand]
    commands.addBinding.to[ListResourcesCommand]
    commands.addBinding.to[MidiCommand]
    commands.addBinding.to[ShowServiceCommand[Logger]]
    commands.addBinding.to[ConfigureServiceCommand[Logger]]
    commands.addBinding.to[ShowServiceCommand[RecordingDevice]]
    commands.addBinding.to[ConfigureServiceCommand[RecordingDevice]]
    commands.addBinding.to[ShowServiceCommand[PlaybackDevice]]
    commands.addBinding.to[ConfigureServiceCommand[PlaybackDevice]]
    commands.addBinding.to[QuantizeCommand]
    commands.addBinding.to[QuitCommand]

    bind[ConfigDescription[Logger]].toInstance(ConfigDescription("logger", "the system loggers"))
    bind[ConfigDescription[RecordingDevice]].toInstance(ConfigDescription("record", "the system input device"))
    bind[ConfigDescription[PlaybackDevice]].toInstance(ConfigDescription("playback", "the system playback devices"))

    ScalaMapBinder.newMapBinder[String, RecordingDevice](binder)
      .addBinding("terminal").to[TerminalInputDevice]

    ScalaMapBinder.newMapBinder[String, Logger](binder)
      .addBinding("terminal").to[TerminalLogger]

    ScalaMapBinder.newMapBinder[String, PlaybackDevice](binder)
      .addBinding("terminal").to[TerminalPlaybackDevice]
  }

}
