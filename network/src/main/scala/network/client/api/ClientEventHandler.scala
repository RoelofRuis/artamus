package network.client.api

trait ClientEventHandler[E] {

  def handleEvent(event: E): Unit

  def receivedInvalidEvent(cause: Throwable): Unit

}
