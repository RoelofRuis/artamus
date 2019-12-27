package protocol.server.api

import protocol.DataResponse

trait ServerAPI {

  def connectionOpened(connection: ConnectionHandle): Unit

  def connectionClosed(connection: ConnectionHandle): Unit

  def afterRequest(connection: ConnectionHandle, response: DataResponse): DataResponse

  def handleRequest(connection: ConnectionHandle, obj: Object): DataResponse

}
