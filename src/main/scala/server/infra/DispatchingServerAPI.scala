package server.infra

import java.util.concurrent.ConcurrentHashMap

import _root_.server.Request
import _root_.server.actions.control.Authenticate
import _root_.server.model.Users._
import com.typesafe.scalalogging.LazyLogging
import music.model.write.user.User
import protocol.Exceptions._
import protocol._
import protocol.server.api.{ConnectionRef, ServerAPI}
import storage.api.{Database, DbIO, NotFound, Transaction}

import scala.util.{Failure, Success, Try}

final class DispatchingServerAPI(
  db: Database,
  server: ServerBindings,
  hooks: ConnectionLifetimeHooks
) extends ServerAPI with LazyLogging {

  private var connections: Map[ConnectionRef, Option[User]] = Map()
  private val transactions: ConcurrentHashMap[ConnectionRef, Transaction] = new ConcurrentHashMap[ConnectionRef, Transaction]()

  def connectionOpened(connection: ConnectionRef): Unit = {
    connections += (connection -> None)
  }

  def connectionClosed(connection: ConnectionRef): Unit = {
    connections -= connection
    server.unsubscribeEvents(connection.toString)
    transactions.remove(connection)
  }

  override def afterRequest(connection: ConnectionRef, response: DataMessage): DataMessage = {
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
              DataMessage(Left(StorageException))
          }
        }

      case Left(_) =>
        transactions.remove(connection)
        response
    }
  }

  def handleRequest(connection: ConnectionRef, request: Object): DataMessage = {
    val result = connections.get(connection) match {
      case None =>
        logger.error(s"Received message on unbound connection [$connection]")
        Left(InvalidStateException)

      case Some(identifier) =>
        Try { request.asInstanceOf[RequestMessage] } match {
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
    DataMessage(result)
  }

  private def authenticate(connection: ConnectionRef, request: RequestMessage): Either[ServerException, Any] = {
    request match {
      case CommandMessage(Authenticate(userName)) =>
        db.getUserByName(userName) match {
          case Right(user) =>
            server.subscribeEvents(connection.toString, event => connection.sendEvent(event))
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

  private def executeRequest(connection: ConnectionRef, user: User, request: RequestMessage): Either[ServerException, Any] = {
    try {
      val transaction = startTransaction(connection)
      val response = request match {
        case CommandMessage(command) => server.handleCommand(Request(user, transaction, command))
        case QueryMessage(query) => server.handleQuery(Request(user, transaction, query))
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

  private def startTransaction(connection: ConnectionRef): DbIO with Transaction = {
    val transaction = db.newTransaction
    transactions.put(connection, transaction)
    transaction
  }
}
