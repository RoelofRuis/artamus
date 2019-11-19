package protocol.transport.server

final case class Connection(id: Long) {

  def name: String = s"connection_$id"

}
