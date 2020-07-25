package network.server.api

import network.Exceptions.ResponseException

trait ServerAPI[R, E] {

  def serverStarted(): Unit

  def serverShuttingDown(error: Option[Throwable] = None): Unit

  def connectionOpened(connection: ConnectionHandle[E]): Unit

  def connectionClosed(connection: ConnectionHandle[E], error: Option[Throwable]): Unit

  def receiveFailed(connection: ConnectionHandle[E], cause: Throwable): Unit

  def handleRequest(connection: ConnectionHandle[E], request: R): Either[ResponseException, Any]

}
