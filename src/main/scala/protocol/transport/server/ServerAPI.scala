package protocol.transport.server

import protocol.v2.DataResponse2

trait ServerAPI {

  def connectionOpened(connection: Connection): Unit

  def connectionClosed(connection: Connection): Unit

  def afterRequest(connection: Connection, response: DataResponse2): DataResponse2

  def handleRequest(connection: Connection, obj: Object): DataResponse2

}
