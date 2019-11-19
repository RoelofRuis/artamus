package protocol.transport.server

trait ServerAPI {

  def connectionAccepted(connectionId: Connection, callback: Any => Unit): Unit

  def connectionDropped(connectionId: Connection): Unit

  def handleRequest(obj: Object): Any

}
