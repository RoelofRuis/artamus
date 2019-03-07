package application.ports

import application.model.Idea

trait Storage[A] {

  def getNextID: Idea.ID

  def put(thing: A): Unit

  def getAll: Vector[A]

}