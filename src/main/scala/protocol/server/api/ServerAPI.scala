package protocol.server.api

import protocol.DataMessage

trait ServerAPI {

  def connectionOpened(connection: ConnectionRef): Unit

  def connectionClosed(connection: ConnectionRef): Unit

  def afterRequest(connection: ConnectionRef, response: DataMessage): DataMessage

  def handleRequest(connection: ConnectionRef, obj: Object): DataMessage

}
