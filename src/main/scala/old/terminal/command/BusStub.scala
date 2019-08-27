package old.terminal.command

import server.api.Commands.Command

import scala.reflect.runtime.universe.TypeTag
import scala.util.Try

// TODO: use socket communication
trait BusStub {

  def execute[C <: Command: TypeTag](command: C): Try[C#Res]

}
