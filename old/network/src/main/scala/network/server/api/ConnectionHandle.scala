package network.server.api

import java.util.UUID

import network.Exceptions.WriteException

trait ConnectionHandle[E] {
  val id: UUID

  def sendEvent(event: E): Option[WriteException]

  override final def toString: String = s"Connection($id)"
}
