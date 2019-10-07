package transport.server

import protocol.DataResponse

trait ServerBindings {

  def connectionAccepted(connectionId: String)

  def connectionDropped(connectionId: String)

  def handleRequest(obj: Object): DataResponse

}
