package protocol

import server.ProtocolServerBindings
import transport.server.SimpleServer

object DefaultServer {

  def apply(port: Int, bindings: ProtocolServerBindings): ServerInterface = {
    new ServerAdaptor(SimpleServer.apply(port, bindings))
  }

  private class ServerAdaptor(server: SimpleServer) extends ServerInterface {
    override def accept(): Unit = server.accept()

    override def shutdown(): Unit = server.shutdown()
  }

}