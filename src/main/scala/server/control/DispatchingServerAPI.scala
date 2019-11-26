package server.control

import com.typesafe.scalalogging.LazyLogging
import music.domain.user.{User, UserRepository}
import protocol._
import protocol.transport.server.{Connection, ServerAPI}
import server.{Request, ServerBindings}

import scala.util.{Failure, Success, Try}

final class DispatchingServerAPI(
  userRepository: UserRepository,
  server: ServerBindings,
) extends ServerAPI with LazyLogging {

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
      case None =>
        logger.error(s"Received message on unbound connection [$connection]")
        Left(s"Received message on unbound connection")

      case Some(identifier) =>
        Try { request.asInstanceOf[ServerRequest] } match {
          case Success(request) =>
            identifier match {
              case None => authenticate(connection, request)
              case Some(user) => handleRequest(user, request)
            }
          case Failure(ex) =>
            logger.warn(s"Unable to read message.", ex)
            Left(s"Unable to read message")
        }
    }
    DataResponse(result)
  }

  def authenticate(connection: Connection, request: ServerRequest): Either[ServerException, Any] = {
    request match {
      case CommandRequest(Authenticate(userName)) =>
        userRepository.getByName(userName) match {
          case None =>
            logger.info(s"User [$userName] not found")
            Left(s"User not found")

          case Some(user) =>
            server.subscribeEvents(connection.name, event => connection.sendEvent(EventResponse(event)))
            connections = connections.updated(connection, Some(user))
            Right(true)
        }
      case _ =>
        logger.warn("Received unauthorized request")
        Left("Unauthorized")
    }
  }

  def handleRequest(user: User, request: ServerRequest): Either[ServerException, Any] = {
    val response = request match {
      case CommandRequest(command) => server.handleCommand(Request(user, command))
      case QueryRequest(query) => server.handleQuery(Request(user, query))
    }
    response match {
      case err @ Left(ex) =>
        logger.error("Internal server error", ex)
        err
      case response => response
    }
  }
}
