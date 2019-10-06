package server

import protocol.{Command, Query}

package object control {

  // Control
  case class Disconnect(shutdownServer: Boolean) extends Command

  case object PublishChanges extends Command

  // Queries
  case object GetDomainListeners extends Query { type Res = List[String] }

}
