package server.handler

import javax.inject.Inject
import protocol.ServerInterface.ControlDispatcher
import protocol.{Control, ServerInterface}
import server.api.Server.Disconnect

private[server] class ControlDispatcherImpl @Inject() (server: ServerInterface) extends ControlDispatcher {

  def handle[A <: Control](message: A): Boolean = {
    message match {
      case Disconnect(false) => server.closeActiveConnection()
      case Disconnect(true) => server.stopServer()
    }
    true
  }

}


