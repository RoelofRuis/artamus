package protocol.client.api

final case class ClientConfig(
  host: String,
  port: Int,
  connectEagerly: Boolean
)
