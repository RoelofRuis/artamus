package protocol.server.api

import protocol.DataResponse

trait ServerAPI {

  def serverStarted(): Unit

  def serverShuttingDown(error: Option[Throwable] = None): Unit

  def connectionOpened(connection: ConnectionHandle): Unit

  def connectionClosed(connection: ConnectionHandle, error: Option[Throwable]): Unit

  def afterRequest(connection: ConnectionHandle, response: DataResponse): DataResponse

  def handleRequest(connection: ConnectionHandle, obj: Object): DataResponse

}
