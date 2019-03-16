package interaction.terminal.command

import application.controller.TrackController
import javax.inject.Inject

class StartRecordingCommand @Inject() (controller: TrackController) extends Command {

  val name = "rec"
  val helpText = "Start recording"

  def run(args: Array[String]): CommandResponse = {
    controller
      .startRecording
      .fold(
        ex => display(s"$ex"),
        _ => display("Recording started")
      )
  }
}
