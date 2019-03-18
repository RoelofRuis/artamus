package interaction.terminal.command

import application.MessageBus
import application.command.TrackCommand.StartRecording
import javax.inject.Inject

class StartRecordingCommand @Inject() (messageBus: MessageBus) extends Command {

  val name = "rec"
  val helpText = "Start recording"

  def run(args: Array[String]): CommandResponse = {
    messageBus.execute(StartRecording)
      .fold(
        ex => display(s"$ex"),
        _ => display("Recording started")
      )
  }
}
