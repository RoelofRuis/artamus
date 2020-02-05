package network.client.api

trait ClientAPI[E] {

  def receivedEvent(event: E): Unit

  def connectingStarted(): Unit

  def connectingFailed(cause: Throwable): Unit

  def connectionEstablished(): Unit

  def connectionLost(cause: Throwable): Unit

}
