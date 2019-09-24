package protocol.server

import protocol.Event

trait ServerConnection {
  def sendEvent[A <: Event](message: A): Unit
  def handleNext(): Unit
  def isOpen: Boolean
  def close(): Unit
}
