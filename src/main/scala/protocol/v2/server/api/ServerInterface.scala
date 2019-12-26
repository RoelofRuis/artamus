package protocol.v2.server.api

trait ServerInterface {

  def accept(): Unit

  def shutdown(): Unit

}
