package application.controller

import application.model.Idea
import application.model.Idea.Idea_ID
import application.model.repository.IdeaRepository
import javax.inject.Inject

import scala.util.{Success, Try}

trait IdeaController {

  def get(id: Idea_ID): Option[Idea]

  def create(title: String): Try[Idea]

}

private[application] class IdeaControllerImpl @Inject() (ideaRepository: IdeaRepository) extends IdeaController {

  def get(id: Idea_ID): Option[Idea] = ideaRepository.get(id)

  def create(title: String): Try[Idea] = Success(ideaRepository.add(title))

}
