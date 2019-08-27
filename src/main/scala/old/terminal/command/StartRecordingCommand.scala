package old.terminal.command

import server.api.Commands.StartRecording

class StartRecordingCommand extends Command {

  val name = "rec"
  val helpText = "Start recording"

  def execute(bus: BusStub, args: Array[String]): CommandResponse = {
    bus.execute(StartRecording)
      .fold(
        ex => display(s"$ex"),
        _ => display("Recording started")
      )
  }
}
