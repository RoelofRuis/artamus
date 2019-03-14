package application.controller

import application.model.Idea
import application.model.repository.IdeaRepository
import javax.inject.Inject

trait IdeaController {

  def getAll: Vector[Idea]

  def create(title: String): Idea

}

private[application] class IdeaControllerImpl @Inject() (ideaRepository: IdeaRepository) extends IdeaController {

  def getAll: Vector[Idea] = ideaRepository.getAll

  def create(title: String): Idea = ideaRepository.add(title)

}
