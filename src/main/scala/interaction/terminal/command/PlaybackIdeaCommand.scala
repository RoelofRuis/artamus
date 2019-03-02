package interaction.terminal.command

import core.ID
import core.components.PlaybackDevice
import core.idea.IdeaRepository
import core.musicdata.MusicData
import interaction.terminal.Prompt
import javax.inject.Inject

class PlaybackIdeaCommand @Inject() (
  prompt: Prompt,
  ideaRepository: IdeaRepository,
  playbackDevice: PlaybackDevice
) extends Command {

  val name = "playback"
  override val helpText = "Playback an idea"

  def run(): CommandResponse = {
    val id = prompt.read("Input the idea ID")

    ideaRepository.loadMusicData(ID(id.toLong)) match {
      case None => display(s"No data for idea with ID [$id]")
      case Some(data) =>
        playbackDevice.play(data)
        display(s"Music data: [${printSeq(data)}]")
    }
  }

  private def printSeq(data: Vector[MusicData]): String = data.map(_.value).mkString(",")

}
