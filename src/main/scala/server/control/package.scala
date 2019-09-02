package server

import protocol.Control

package object control {

  case class Disconnect(shutdownServer: Boolean) extends Control

}
