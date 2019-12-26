package protocol.v2.server.api

import java.util.UUID

import protocol.v2.Exceptions.TransportException

trait ConnectionReference {
  val id: UUID

  // TODO: see if event can be more narrowly typed
  def sendEvent(event: Any): Option[TransportException]

  override final def toString: String = s"Connection($id)"
}
