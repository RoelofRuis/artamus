package protocol.v2.api

trait ClientInterface2 {
  def sendCommand[A <: Command2](command: A): Option[ResponseException]

  def sendQuery[A <: Query2](query: A): Either[ResponseException, A#Res]
}
