package interaction.terminal.command

import application.MessageBus
import application.command.TrackCommand.Play
import application.model._
import javax.inject.Inject

import scala.util.Try

class PlayTrackCommand @Inject() (messageBus: MessageBus) extends Command {

  val name = "play"
  val helpText = "Play a track"
  override val argsHelp = Some("[id: Int]")

  def run(args: Array[String]): CommandResponse = {
    val res = for {
      id <- Try(ID[Track](args(0).toLong))
    } yield {
      if (messageBus.execute(Play(id)).isSuccess) continue
      else display(s"Unknown track ID [$id]")
    }

    returnRecovered(res)
  }
}
