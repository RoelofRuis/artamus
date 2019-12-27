package protocol.server.api

trait ServerInterface {

  def accept(): Unit

  def shutdown(): Unit

}
