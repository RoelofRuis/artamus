package interaction.terminal.command

import interaction.terminal.Prompt
import core.idea.IdeaRepository
import core.musicdata.MusicDataStreamer
import javax.inject.Inject

class CreateIdeaCommand @Inject() (
  prompt: Prompt,
  ideaRepository: IdeaRepository,
  musicDataStreamer: MusicDataStreamer
) extends Command {

  val name = "idea"
  val helpText = "Write down a new idea"

  def run(args: Array[String]): CommandResponse = {
    val title = prompt.read("Idea title")

    val idea = ideaRepository.add(title)

    musicDataStreamer.run(idea)

    display(s"Created idea [$title] with id [${idea.id}]")
  }

}
