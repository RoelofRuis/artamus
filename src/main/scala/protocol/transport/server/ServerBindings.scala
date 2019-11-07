package protocol.transport.server

trait ServerBindings {

  def connectionAccepted(connectionId: String, callback: Any => Unit): Unit

  def connectionDropped(connectionId: String): Unit

  def handleRequest(obj: Object): Any

}
