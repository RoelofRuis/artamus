package application.api

import scala.reflect.runtime.universe.TypeTag
import application.api.Commands.Command

import scala.util.Try

trait CommandBus {

  def execute[C <: Command: TypeTag](command: C): Try[C#Res]

}
