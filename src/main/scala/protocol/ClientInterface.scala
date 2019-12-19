package protocol

trait ClientInterface {
  def sendCommand[A <: Command](message: A): Boolean

  def sendQuery[A <: Query](message: A): Option[A#Res]

  def open(): Unit

  def close(): Unit
}
