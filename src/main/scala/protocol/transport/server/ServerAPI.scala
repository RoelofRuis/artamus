package protocol.transport.server

import protocol.DataResponse

trait ServerAPI {

  def connectionOpened(connection: Connection): Unit

  def connectionClosed(connection: Connection): Unit

  def afterRequest(connection: Connection, response: DataResponse): DataResponse

  def handleRequest(connection: Connection, obj: Object): DataResponse

}
