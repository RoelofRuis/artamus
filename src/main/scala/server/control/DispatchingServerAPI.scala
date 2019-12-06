package server.control

import com.typesafe.scalalogging.LazyLogging
import music.domain.user.{User, UserRepository}
import protocol._
import protocol.transport.server.{Connection, ServerAPI}
import server.storage.{EntityNotFoundException, TransactionalDB}
import server.{Request, ServerBindings}

import scala.util.{Failure, Success, Try}

final class DispatchingServerAPI(
  db: TransactionalDB,
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

  override def afterRequest(connection: Connection, response: DataResponse): DataResponse = {
    response.data match {
      case Right(_) =>
        db.commit() match {
          case Success(()) => response
          case Failure(ex) =>
            logger.error(s"Unable to commit changes", ex)
            DataResponse(Left(s"Unable to commit changes"))
        }

      case Left(_) =>
        db.rollback()
        response
    }
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
          case Success(user) =>
            server.subscribeEvents(connection.name, event => connection.sendEvent(EventResponse(event)))
            connections = connections.updated(connection, Some(user))
            Right(true)

          case Failure(_: EntityNotFoundException) =>
            logger.info(s"User [$userName] not found")
            Left(s"User not found")

          case Failure(ex) =>
            logger.error("Error in server logic", ex)
            Left("Server error")
        }
      case _ =>
        logger.warn("Received unauthorized request")
        Left("Unauthorized")
    }
  }

  def handleRequest(user: User, request: ServerRequest): Either[ServerException, Any] = {
    try {
      val response = request match {
        case CommandRequest(command) => server.handleCommand(Request(user, command))
        case QueryRequest(query) => server.handleQuery(Request(user, query))
      }
      response match {
        case Failure(ex) =>
          logger.error("Error in server logic", ex)
          Left("Server error")
        case Success(response) => Right(response)
      }
    } catch {
      case ex: Exception =>
        logger.error("Unexpected server error", ex)
        Left("Server error")
    }
  }
}
