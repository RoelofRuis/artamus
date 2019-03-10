package application.ports

/**
  * Simple logging interface for logging system related messages.
  */
trait Logger {

  def debug(text: String): Unit

}
