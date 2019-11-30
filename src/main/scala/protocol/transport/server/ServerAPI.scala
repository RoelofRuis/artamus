package protocol.transport.server

trait ServerAPI {

  def connectionOpened(connection: Connection): Unit

  def connectionClosed(connection: Connection): Unit

  // TODO: add beforeRequest
  // TODO: add afterRequest

  def handleRequest(connection: Connection, obj: Object): Any

}
