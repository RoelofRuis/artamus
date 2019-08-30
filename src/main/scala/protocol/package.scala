package object protocol {

  private[protocol] sealed trait ServerRequestMessage
  private[protocol] case object ControlMessage extends ServerRequestMessage
  private[protocol] case object CommandMessage extends ServerRequestMessage

  private[protocol] sealed trait ServerResponseMessage
  private[protocol] case object ResponseMessage extends ServerResponseMessage
  private[protocol] case object EventMessage extends ServerResponseMessage

  // Public API
  trait Event

  trait Control

  trait Command

  trait Query {
    type Res
  }

  trait Server {

    def acceptConnections(commandHandler: Command => Boolean, controlHandler: Control => Boolean): Unit

    def publishEvent[A <: Event](event: A): Unit

    def closeActiveConnection(): Unit

    def stopServer(): Unit

  }

  def client(port: Int): Client = new Client(port)
  def server(port: Int): Server = new SingleConnectionServer(port)

}
