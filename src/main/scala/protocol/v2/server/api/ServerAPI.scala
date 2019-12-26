package protocol.v2.server.api

import protocol.v2.DataResponse2

trait ServerAPI {

  def connectionOpened(connection: ConnectionRef): Unit

  def connectionClosed(connection: ConnectionRef): Unit

  def afterRequest(connection: ConnectionRef, response: DataResponse2): DataResponse2

  def handleRequest(connection: ConnectionRef, obj: Object): DataResponse2

}
