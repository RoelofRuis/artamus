package network.client.api

import network.DataResponseMessage

trait ClientCallbacks {

  def connectionEstablished(): Unit

  def connectionLost(cause: Throwable): Unit

  def receivedUnexpectedResponse(obj: DataResponseMessage): Unit

}
