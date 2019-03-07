package application.idea

import application.idea.Idea.ID

case class Idea(id: ID, title: String)

object Idea {

  case class ID(id: Long) extends AnyVal {
    override def toString: String = id.toString
  }

}