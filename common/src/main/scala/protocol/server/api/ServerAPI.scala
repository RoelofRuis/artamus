package protocol.server.api

import protocol.Exceptions.ResponseException

trait ServerAPI[E] {

  def serverStarted(): Unit

  def serverShuttingDown(error: Option[Throwable] = None): Unit

  def connectionOpened(connection: ConnectionHandle[E]): Unit

  def connectionClosed(connection: ConnectionHandle[E], error: Option[Throwable]): Unit

  def afterRequest(connection: ConnectionHandle[E], response: Either[ResponseException, Any]): Either[ResponseException, Any]

  def handleRequest(connection: ConnectionHandle[E], obj: Object): Either[ResponseException, Any]

}
