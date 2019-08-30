package server.handler

import javax.inject.Inject
import protocol.{Control, ControlHandler, Server}
import server.api.Server.Disconnect

private[server] class ControlHandlerImpl @Inject() (server: Server) extends ControlHandler {

  def handle[A <: Control](message: A): Boolean = {
    message match {
      case Disconnect(false) => server.closeActiveConnection()
      case Disconnect(true) => server.stopServer()
    }
    true
  }

}


