package protocol.transport.server

@deprecated
trait Connection {
  val id: Long

  def name: String
  def sendEvent(event: Any): Unit

  override final def toString: String = s"Connection($id)"
}
