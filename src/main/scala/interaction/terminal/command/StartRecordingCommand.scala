package interaction.terminal.command

import application.ports.MessageBus
import application.command.TrackCommand.StartRecording

class StartRecordingCommand extends Command {

  val name = "rec"
  val helpText = "Start recording"

  def execute(bus: MessageBus, args: Array[String]): CommandResponse = {
    bus.execute(StartRecording)
      .fold(
        ex => display(s"$ex"),
        _ => display("Recording started")
      )
  }
}
