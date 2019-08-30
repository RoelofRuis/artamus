package server.handler

import javax.inject.Inject
import protocol.{Control, ControlDispatcher, Server}
import server.api.Server.Disconnect

private[server] class ControlDispatcherImpl @Inject() (server: Server) extends ControlDispatcher {

  def handle[A <: Control](message: A): Boolean = {
    message match {
      case Disconnect(false) => server.closeActiveConnection()
      case Disconnect(true) => server.stopServer()
    }
    true
  }

}


