package server.control

import java.util.concurrent.ConcurrentHashMap

import com.typesafe.scalalogging.LazyLogging
import music.domain.user.User
import protocol._
import protocol.transport.server.{Connection, ServerAPI}
import server.{Request, ServerBindings}
import server.storage.api.{Db, DbIO, DbRead, DbTransaction}
import server.storage.entity.NotFound
import server.storage.entity.Users._

import scala.util.{Failure, Success, Try}

final class DispatchingServerAPI(
  db2: Db with DbRead,
  server: ServerBindings,
) extends ServerAPI with LazyLogging {

  private var connections: Map[Connection, Option[User]] = Map()
  private val transactions: ConcurrentHashMap[Connection, DbTransaction] = new ConcurrentHashMap[Connection, DbTransaction]()

  def connectionOpened(connection: Connection): Unit = {
    connections += (connection -> None)
  }

  def connectionClosed(connection: Connection): Unit = {
    connections -= connection
    server.unsubscribeEvents(connection.name)
    transactions.remove(connection)
  }

  override def afterRequest(connection: Connection, response: DataResponse): DataResponse = {
    response.data match {
      case Right(_) =>
        Option(transactions.get(connection)) match {
          case None => response
          case Some(transaction) => transaction.commit() match {
            case Right(numChanges) => logger.debug(s"Committed [$numChanges] changes")
              response

            case Left(ex) => logger.error(s"Unable to commit changes", ex)
              DataResponse(Left(s"Unable to commit changes"))
          }
        }

      case Left(_) =>
        transactions.remove(connection)
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
              case Some(user) => handleRequest(connection, user, request)
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
        db2.getUserByName(userName) match {
          case Right(user) =>
            server.subscribeEvents(connection.name, event => connection.sendEvent(EventResponse(event)))
            connections = connections.updated(connection, Some(user))
            Right(true)

          case Left(NotFound()) =>
            logger.info(s"User [$userName] not found")
            Left(s"User not found")

          case Left(ex) =>
            logger.error("Error in server logic", ex)
            Left("Server error")
        }
      case _ =>
        logger.warn("Received unauthorized request")
        Left("Unauthorized")
    }
  }

  def handleRequest(connection: Connection, user: User, request: ServerRequest): Either[ServerException, Any] = {
    try {
      val transaction = startTransaction(connection)
      val response = request match {
        case CommandRequest(command) => server.handleCommand(Request(user, transaction, command))
        case QueryRequest(query) => server.handleQuery(Request(user, transaction, query))
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

  private def startTransaction(connection: Connection): DbIO with DbTransaction = {
    val transaction = db2.newTransaction
    transactions.put(connection, transaction)
    transaction
  }
}
