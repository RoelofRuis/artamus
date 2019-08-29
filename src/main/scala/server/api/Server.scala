package server.api

import protocol.Control

object Server {

  case class Disconnect(shutdownServer: Boolean) extends Control

}
