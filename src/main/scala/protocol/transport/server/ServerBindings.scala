package protocol.transport.server

trait ServerBindings {

  def connectionAccepted(connectionId: String, callback: Any => Unit)

  def connectionDropped(connectionId: String)

  def handleRequest(obj: Object): Any

}
