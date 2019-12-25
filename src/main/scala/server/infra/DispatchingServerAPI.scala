package server.infra

import java.util.concurrent.ConcurrentHashMap

import com.typesafe.scalalogging.LazyLogging
import music.model.write.user.User
import protocol._
import protocol.transport.server.{Connection, ServerAPI}
import server.Request
import server.actions.control.Authenticate
import server.model.Users._
import storage.api.{Database, DbIO, Transaction, NotFound}

import scala.util.{Failure, Success, Try}

final class DispatchingServerAPI(
  db: Database,
  server: ServerBindings,
  hooks: ConnectionLifetimeHooks
) extends ServerAPI with LazyLogging {

  private var connections: Map[Connection, Option[User]] = Map()
  private val transactions: ConcurrentHashMap[Connection, Transaction] = new ConcurrentHashMap[Connection, Transaction]()

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
              ex.causes.zipWithIndex.foreach { case (err, idx) => logger.error(s"commit error [$idx]", err) }
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
        db.getUserByName(userName) match {
          case Right(user) =>
            server.subscribeEvents(connection.name, event => connection.sendEvent(EventResponse(event)))
            connections = connections.updated(connection, Some(user))
            hooks.onAuthenticated(startTransaction(connection), user)
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

  private def startTransaction(connection: Connection): DbIO with Transaction = {
    val transaction = db.newTransaction
    transactions.put(connection, transaction)
    transaction
  }
}
