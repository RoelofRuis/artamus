package server

import protocol._
import protocol.transport.server.{Connection, ServerAPI}

import scala.util.Try

final case class DispatchingServerAPI(
  server: ServerBindings,
) extends ServerAPI {

  def connectionAccepted(connection: Connection): Unit = {
    server.subscribeEvents(connection.name, connection.sendEvent)
  }

  def connectionDropped(connection: Connection): Unit = {
    server.unsubscribeEvents(connection.name)
  }

  def handleRequest(connection: Connection, request: Object): DataResponse = {
    val result = tryRead[ServerRequest](request).toEither match {
      case Right(CommandRequest(command)) => server.handleCommand(command)
      case Right(QueryRequest(query)) => server.handleQuery(query)
      case Left(ex) => Left(s"Unable to determine message type. [$ex]")
    }
    DataResponse(result)
  }

  private def tryRead[A](obj: Object): Try[A] = Try { obj.asInstanceOf[A] }
}
