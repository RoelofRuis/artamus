package application.handler

import application.api.Commands.{Command, CreateIdea, GetIdea}
import application.domain.repository.IdeaRepository
import javax.inject.Inject

import scala.util.{Success, Try}

private[application] class IdeaCommandHandler @Inject() (ideaRepository: IdeaRepository) extends CommandHandler {

  def handle[Res]: PartialFunction[Command[Res], Try[Res]] = {
    case GetIdea(id) => ideaRepository.get(id)
    case CreateIdea(title) => Success(ideaRepository.add(title))
  }

}
