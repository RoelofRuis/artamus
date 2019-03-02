package io.cmd.command

import io.cmd.Prompt
import core.IdeaRepository
import javax.inject.Inject

class IdeaCommand @Inject() (prompt: Prompt, ideaRepository: IdeaRepository) extends Command {

  val name = "idea"
  override val helpText = "Write down a new idea"

  def run(): CommandResponse = {
    val title = prompt.read("Idea title")

    val id = ideaRepository.add(title)

    display(s"Created idea [$title] with id [$id]")
  }

}
