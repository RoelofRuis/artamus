package interaction.terminal

import application.api.{Driver, RecordingDevice}
import interaction.terminal.command._
import interaction.terminal.device.TerminalInputDevice
import net.codingwell.scalaguice.{ScalaMapBinder, ScalaModule, ScalaMultibinder}

class TerminalModule extends ScalaModule {

  override def configure(): Unit = {
    ScalaMapBinder.newMapBinder[String, Driver](binder)
      .addBinding("terminal").to[TerminalDriver]

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
