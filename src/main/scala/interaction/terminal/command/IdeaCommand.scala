package interaction.terminal.command

import interaction.terminal.Prompt
import core.idea.IdeaRepository
import core.musicdata.MusicDataStreamer
import javax.inject.Inject

class IdeaCommand @Inject() (
  prompt: Prompt,
  ideaRepository: IdeaRepository,
  musicDataStreamer: MusicDataStreamer
) extends Command {

  val name = "idea"
  override val helpText = "Write down a new idea"

  def run(): CommandResponse = {
    val title = prompt.read("Idea title")

    val id = ideaRepository.add(title)

    musicDataStreamer.run()

    display(s"Created idea [$title] with id [$id]")
  }

}
