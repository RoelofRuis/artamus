package interaction.terminal.command

import application.MessageBus
import application.command.TrackCommand.StoreRecorded
import application.model.{ID, Idea}
import javax.inject.Inject

import scala.util.Try

class StoreRecordingCommand @Inject() (messageBus: MessageBus) extends Command {

  val name = "store"
  val helpText = "Store the currently running recording"
  override val argsHelp = Some("[idea id: Int]")

  def run(args: Array[String]): CommandResponse = {
    val res = for {
      ideaId <- Try(ID[Idea](args(0).toLong))
      track <- messageBus.execute(StoreRecorded(ideaId))
    } yield track

    res.fold(
      ex => display(s"Could not store recording: $ex"),
      track => display(s"Stored ${track.id} with num events [${track.elements.size}]")
    )
  }
}
