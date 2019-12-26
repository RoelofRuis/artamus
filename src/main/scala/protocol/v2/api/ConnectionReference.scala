package protocol.v2.api

import java.util.UUID

trait ConnectionReference {
  val id: UUID

  // TODO: see if event can be more narrowly typed
  def sendEvent(event: Any): Option[TransportException]

  override final def toString: String = s"Connection($id)"
}
