package network.client.api

import network.Exceptions.ResponseException

trait ClientCallbacks {

  def connectionEstablished(): Unit

  def connectionLost(cause: Throwable): Unit

  def receivedUnexpectedResponse(obj: Either[ResponseException, Any]): Unit

}
