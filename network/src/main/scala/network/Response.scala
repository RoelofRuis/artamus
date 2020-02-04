package network

import network.Exceptions.ResponseException

private[network] sealed trait ResponseMessage
private[network] final case class DataResponseMessage(data: Either[ResponseException, Any]) extends ResponseMessage
private[network] final case class EventResponseMessage[A](event: A) extends ResponseMessage
