package io.cmd.command

import core.IdeaRepository
import javax.inject.Inject

class ListIdeasCommand @Inject() (ideaRepository: IdeaRepository) extends Command {

  val name = "list"
  override val helpText = "List the available ideas"

  def run(): CommandResponse = {
    val response = ideaRepository
      .getAll
      .zipWithIndex
      .map { case (idea, index) => s"$index. : ${idea.title}"}
      .mkString("\n")

    display(response)
  }

}
