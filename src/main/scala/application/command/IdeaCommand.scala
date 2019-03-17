package application.command

import application.model.Idea
import application.model.Idea.Idea_ID

object IdeaCommand {

  case class GetIdea(id: Idea_ID) extends Command[Idea]

  case class CreateIdea(title: String) extends Command[Idea]

}
