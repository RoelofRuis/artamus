package transport.server

trait ServerBindings {

  def connectionAccepted(connectionId: String)

  def connectionDropped(connectionId: String)

  def handleRequest(obj: Object): Any

}
