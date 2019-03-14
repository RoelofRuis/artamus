package application.model

import application.model.Idea.Idea_ID

case class Idea(id: Idea_ID, title: String)

case object Idea {

  type Idea_ID = ID[Idea.type]

}