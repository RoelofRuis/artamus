package server.infra

import java.util.concurrent.ConcurrentHashMap

import com.typesafe.scalalogging.LazyLogging
import music.model.write.user.User
import protocol.transport.server.{Connection, ServerAPI}
import protocol.v2.Exceptions.{InvalidRequest, InvalidStateException, LogicException, MessageException, ServerException, StorageException, Unauthorized}
import protocol.v2._
import _root_.server.Request
import _root_.server.actions.control.Authenticate
import _root_.server.model.Users._
import storage.api.{Database, DbIO, NotFound, Transaction}

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

  override def afterRequest(connection: Connection, response: DataResponse2): DataResponse2 = {
    response.data match {
      case Right(_) =>
        Option(transactions.get(connection)) match {
          case None => response

          case Some(transaction) => transaction.commit() match {
            case Right(numChanges) =>
              logger.debug(s"Committed [$numChanges] changes")
              response

            case Left(ex) => logger.error(s"Unable to commit changes", ex)
              ex.causes.zipWithIndex.foreach { case (err, idx) => logger.error(s"commit error [$idx]", err) }
              DataResponse2(Left(StorageException))
          }
        }

      case Left(_) =>
        transactions.remove(connection)
        response
    }
  }

  def handleRequest(connection: Connection, request: Object): DataResponse2 = {
    val result = connections.get(connection) match {
      case None =>
        logger.error(s"Received message on unbound connection [$connection]")
        Left(InvalidStateException)

      case Some(identifier) =>
        Try { request.asInstanceOf[Request2] } match {
          case Success(request) =>
            identifier match {
              case None => authenticate(connection, request)
              case Some(user) => executeRequest(connection, user, request)
            }
          case Failure(ex) =>
            logger.warn(s"Unable to read message.", ex)
            Left(MessageException)
        }
    }
    DataResponse2(result)
  }

  private def authenticate(connection: Connection, request: Request2): Either[ServerException, Any] = {
    request match {
      case CommandRequest2(Authenticate(userName)) =>
        db.getUserByName(userName) match {
          case Right(user) =>
            server.subscribeEvents(connection.name, event => connection.sendEvent(EventResponse2(event)))
            connections = connections.updated(connection, Some(user))
            hooks.onAuthenticated(startTransaction(connection), user)
            Right(true)

          case Left(NotFound()) =>
            logger.info(s"User [$userName] not found")
            Left(InvalidRequest(s"User [$userName] not found"))

          case Left(ex) =>
            logger.error("Error in server logic", ex)
            Left(LogicException)
        }
      case _ =>
        logger.warn("Received unauthorized request")
        Left(Unauthorized)
    }
  }

  private def executeRequest(connection: Connection, user: User, request: Request2): Either[ServerException, Any] = {
    try {
      val transaction = startTransaction(connection)
      val response = request match {
        case CommandRequest2(command) => server.handleCommand(Request(user, transaction, command))
        case QueryRequest2(query) => server.handleQuery(Request(user, transaction, query))
      }
      response match {
        case Failure(ex) =>
          // TODO: is LogicException the correct error here?
          logger.error("Error in server logic", ex)
          Left(LogicException)

        case Success(response) => Right(response)
      }
    } catch {
      case ex: Exception =>
        logger.error("Unexpected server error", ex)
        Left(LogicException)
    }
  }

  private def startTransaction(connection: Connection): DbIO with Transaction = {
    val transaction = db.newTransaction
    transactions.put(connection, transaction)
    transaction
  }
}
