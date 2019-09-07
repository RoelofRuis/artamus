package protocol.client2

trait ClientConnectionFactory {
  def connect(): Either[String, ClientConnection]
}
