package interaction.terminal.command

import server.api.Actions.Action

import scala.reflect.runtime.universe.TypeTag
import scala.util.Try

// TODO: use socket communication
trait BusStub {

  def execute[C <: Action: TypeTag](command: C): Try[C#Res]

}
