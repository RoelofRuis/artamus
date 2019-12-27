package protocol.server.api

import java.util.UUID

import protocol.Event
import protocol.Exceptions.WriteException

trait ConnectionRef {
  val id: UUID

  def sendEvent(event: Event): Option[WriteException]

  override final def toString: String = s"Connection($id)"
}
