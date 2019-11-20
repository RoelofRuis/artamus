package protocol.transport.server

trait ServerAPI {

  def connectionOpened(connection: Connection): Unit

  def connectionClosed(connection: Connection): Unit

  def handleRequest(connection: Connection, obj: Object): Any

}
