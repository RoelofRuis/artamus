package server.control

import music.domain.user.{User, UserRepository}
import protocol._
import protocol.transport.server.{Connection, ServerAPI}
import server.{Request, ServerBindings}

import scala.util.{Failure, Success, Try}

final class DispatchingServerAPI(
  userRepository: UserRepository,
  server: ServerBindings,
) extends ServerAPI {

  private var connections: Map[Connection, Option[User]] = Map()

  def connectionOpened(connection: Connection): Unit = {
    connections += (connection -> None)
  }

  def connectionClosed(connection: Connection): Unit = {
    connections -= connection
    server.unsubscribeEvents(connection.name)
  }

  def handleRequest(connection: Connection, request: Object): DataResponse = {
    val result = connections.get(connection) match {
      case None => Left(s"Received message on unbound connection [$connection]")
      case Some(identifier) =>
        Try { request.asInstanceOf[ServerRequest] } match {
          case Success(request) =>
            identifier match {
              case None => authenticate(connection, request)
              case Some(user) => handleRequest(user, request)
            }
          case Failure(ex) => Left(s"Unable to read message. [$ex]")
        }
    }
    DataResponse(result)
  }

  def authenticate(connection: Connection, request: ServerRequest): Either[ServerException, Any] = {
    request match {
      case CommandRequest(Authenticate(userName)) =>
        userRepository.getByName(userName) match {
          case None => Left(s"User [$userName] not found")
          case Some(user) =>
            server.subscribeEvents(connection.name, event => connection.sendEvent(EventResponse(event)))
            connections = connections.updated(connection, Some(user))
            Right(true)
        }
      case _ => Left("Unauthorized")
    }
  }

  def handleRequest(user: User, request: ServerRequest): Either[ServerException, Any] = {
    request match {
      case CommandRequest(command) => server.handleCommand(command)
      case QueryRequest(query) => server.handleQuery(Request(user, query))
    }
  }
}
