package server.handler

import javax.inject.Inject
import protocol.{Control, Server}
import server.api.Server.Disconnect

private[server] class ControlHandler @Inject() (server: Server) {

  def handle[A <: Control](message: A): Boolean = {
    message match {
      case Disconnect(false) => server.closeActiveConnection()
      case Disconnect(true) => server.stopServer()
    }
    true
  }

}


