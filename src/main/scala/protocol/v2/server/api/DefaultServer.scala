package protocol.v2.server.api

object DefaultServer {

  def apply(port: Int, bindings: ServerAPI): ServerInterface = {
    new ServerAdaptor(SimpleServer.apply(port, bindings))
  }

  private class ServerAdaptor(server: SimpleServer) extends ServerInterface {
    override def accept(): Unit = server.accept()

    override def shutdown(): Unit = server.shutdown()
  }

}