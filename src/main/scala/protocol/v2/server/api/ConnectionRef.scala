package protocol.v2.server.api

import java.util.UUID

import protocol.v2.Event2
import protocol.v2.Exceptions.WriteException

trait ConnectionRef {
  val id: UUID

  // TODO: see if event can be more narrowly typed
  def sendEvent(event: Event2): Option[WriteException]

  override final def toString: String = s"Connection($id)"
}
