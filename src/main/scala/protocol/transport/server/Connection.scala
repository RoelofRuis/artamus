package protocol.transport.server

import java.io.ObjectOutputStream

final case class Connection private[server] (
  id: Long,
  private val eventOut: ObjectOutputStream
) {

  def name: String = s"connection_$id"

  def sendEvent(event: Any): Unit = eventOut.writeObject(event)

}
