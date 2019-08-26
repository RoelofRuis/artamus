package interaction.terminal

import application.api.RecordingDevice
import interaction.terminal.command._
import interaction.terminal.device.TerminalInputDevice
import net.codingwell.scalaguice.{ScalaModule, ScalaMultibinder}

class TerminalModule extends ScalaModule {

  override def configure(): Unit = {
    bind[Prompt].to[TerminalPrompt]

    val commands = ScalaMultibinder.newSetBinder[Command](binder)
    commands.addBinding.to[ListTracksCommand]
    commands.addBinding.to[PlayTrackCommand]
    commands.addBinding.to[StartRecordingCommand]
    commands.addBinding.to[StoreRecordingCommand]
    commands.addBinding.to[DevicesCommand]
    commands.addBinding.to[QuantizeCommand]
    commands.addBinding.to[DisplayTrackCommand]
    commands.addBinding.to[QuitCommand]

    bind[RecordingDevice].to[TerminalInputDevice].asEagerSingleton()
  }

}
