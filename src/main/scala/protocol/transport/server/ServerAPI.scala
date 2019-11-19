package protocol.transport.server

trait ServerAPI {

  def connectionAccepted(connection: Connection): Unit

  def connectionDropped(connection: Connection): Unit

  def handleRequest(connection: Connection, obj: Object): Any

}
