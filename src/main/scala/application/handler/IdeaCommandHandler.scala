package application.handler

import application.command.Command
import application.command.IdeaCommand.{CreateIdea, GetIdea}
import application.model.repository.IdeaRepository
import javax.inject.Inject

import scala.util.{Success, Try}

private[application] class IdeaCommandHandler @Inject() (ideaRepository: IdeaRepository) extends CommandHandler {

  def handle[Res]: PartialFunction[Command[Res], Try[Res]] = {
    case GetIdea(id) => ideaRepository.get(id)
    case CreateIdea(title) => Success(ideaRepository.add(title))
  }

}
