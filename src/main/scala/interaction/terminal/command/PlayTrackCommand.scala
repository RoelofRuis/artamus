package interaction.terminal.command

import application.controller.TrackController
import application.model._
import javax.inject.Inject

import scala.util.Try

class PlayTrackCommand @Inject() (controller: TrackController) extends Command {

  val name = "play"
  val helpText = "Play a track"
  override val argsHelp = Some("[id: Int]")

  def run(args: Array[String]): CommandResponse = {
    val res = for {
      id <- Try(ID[Track](args(0).toLong))
    } yield {
      if (controller.play(id)) continue
      else display(s"Unknown track ID [$id]")
    }

    returnRecovered(res)
  }
}
