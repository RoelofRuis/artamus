package server

import protocol.{Control, Query}

package object control {

  // Control
  case class Disconnect(shutdownServer: Boolean) extends Control

  // Queries
  case object GetViews extends Query { type Res = List[String] }

}
