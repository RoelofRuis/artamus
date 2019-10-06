package server

import protocol.Command

package object control {

  // Control
  case class Disconnect(shutdownServer: Boolean) extends Command

  case object CommitChanges extends Command

}
