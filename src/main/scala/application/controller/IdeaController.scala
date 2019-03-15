package application.controller

import application.model.Idea
import application.model.Idea.Idea_ID
import application.model.repository.IdeaRepository
import javax.inject.Inject

trait IdeaController {

  def get(id: Idea_ID): Option[Idea]

  def create(title: String): Idea

}

private[application] class IdeaControllerImpl @Inject() (ideaRepository: IdeaRepository) extends IdeaController {

  def get(id: Idea_ID): Option[Idea] = ideaRepository.get(id)

  def create(title: String): Idea = ideaRepository.add(title)

}
