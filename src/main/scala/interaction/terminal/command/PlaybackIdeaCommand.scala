package interaction.terminal.command

import core.application.ServiceRegistry
import core.components.PlaybackDevice
import core.idea.Idea
import core.musicdata.PartRepository
import interaction.terminal.Prompt
import javax.inject.Inject

class PlaybackIdeaCommand @Inject() (
  prompt: Prompt,
  partRepository: PartRepository,
  playbackRegistry: ServiceRegistry[PlaybackDevice]
) extends Command {

  val name = "playback"
  override val helpText = "Playback an idea"

  def run(): CommandResponse = {
    val id = prompt.read("Input the idea ID")

    partRepository.retrieve(Idea.ID(id.toLong)) match {
      case None => display(s"No Part stored for idea with ID [$id]")
      case Some(data) =>
        playbackRegistry.map(_.play(data))
        continue
    }
  }
}
