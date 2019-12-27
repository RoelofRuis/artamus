package protocol.server.api

import protocol.DataResponse

trait ServerAPI {

  def connectionOpened(connection: ConnectionRef): Unit

  def connectionClosed(connection: ConnectionRef): Unit

  def afterRequest(connection: ConnectionRef, response: DataResponse): DataResponse

  def handleRequest(connection: ConnectionRef, obj: Object): DataResponse

}
