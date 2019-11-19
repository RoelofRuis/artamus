package protocol

import server.DispatchingServerAPI
import transport.server.SimpleServer

object DefaultServer {

  def apply(port: Int, bindings: DispatchingServerAPI): ServerInterface = {
    new ServerAdaptor(SimpleServer.apply(port, bindings))
  }

  private class ServerAdaptor(server: SimpleServer) extends ServerInterface {
    override def accept(): Unit = server.accept()

    override def shutdown(): Unit = server.shutdown()
  }

}