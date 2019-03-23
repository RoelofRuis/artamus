package interaction.terminal.command

import application.api.Commands.StartRecording
import application.api.CommandBus

class StartRecordingCommand extends Command {

  val name = "rec"
  val helpText = "Start recording"

  def execute(bus: CommandBus, args: Array[String]): CommandResponse = {
    bus.execute(StartRecording)
      .fold(
        ex => display(s"$ex"),
        _ => display("Recording started")
      )
  }
}
