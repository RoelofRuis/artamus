package server

import protocol._
import protocol.transport.server.ServerAPI

import scala.util.{Failure, Success, Try}

final case class DispatchingServerAPI(
  server: ServerBindings,
) extends ServerAPI {

  def connectionAccepted(connectionId: String, callback: Any => Unit): Unit = {
    server.subscribeEvents(connectionId, callback)
  }

  def connectionDropped(connectionId: String): Unit = {
    server.unsubscribeEvents(connectionId)
  }

  def handleRequest(request: Object): DataResponse = {
    val result = tryRead[ServerRequest](request).toEither match {
      case Right(CommandRequest(command)) =>
        Try { server.handleCommand(command) } match {
          case Success(response) => response match {
            case Some(res) => Right(res)
            case None => Left(s"No handler defined for command [$command]")
          }
          case Failure(ex) => Left(s"Error during command execution [$ex]")
        }

      case Right(QueryRequest(query)) =>
        Try { server.handleQuery(query) } match {
          case Success(response) => response match {
            case Some(res) => Right(res)
            case None => Left(s"No handler defined for query [$query]")
          }
          case Failure(ex) => Left(s"Error during query execution [$ex]")
        }


      case Left(ex) => Left(s"Unable to determine message type. [$ex]")
    }
    DataResponse(result)
  }

  private def tryRead[A](obj: Object): Try[A] = Try { obj.asInstanceOf[A] }
}
