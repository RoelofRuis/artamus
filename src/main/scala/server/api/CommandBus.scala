package server.api

import scala.reflect.runtime.universe.TypeTag
import server.api.Commands.Command

import scala.util.Try

/** @deprecated remove */
trait CommandBus {

  def execute[C <: Command: TypeTag](command: C): Try[C#Res]

}
