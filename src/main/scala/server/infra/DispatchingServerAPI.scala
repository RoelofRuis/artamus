package server.infra

import java.util.concurrent.ConcurrentHashMap

import _root_.server.Request
import _root_.server.actions.control.Authenticate
import _root_.server.model.Users._
import com.typesafe.scalalogging.LazyLogging
import javax.inject.{Inject, Singleton}
import music.model.write.user.User
import protocol.Exceptions._
import protocol._
import protocol.server.api.{ConnectionHandle, ServerAPI}
import storage.api.{Database, DbIO, NotFound, Transaction}

import scala.util.{Failure, Success, Try}

@Singleton
final class DispatchingServerAPI @Inject() (
  db: Database,
  server: ServerBindings,
  hooks: ConnectionLifetimeHooks
) extends ServerAPI with LazyLogging {

  private var connections: Map[ConnectionHandle, Option[User]] = Map()
  private val transactions: ConcurrentHashMap[ConnectionHandle, Transaction] = new ConcurrentHashMap[ConnectionHandle, Transaction]()

  def connectionOpened(connection: ConnectionHandle): Unit = {
    connections += (connection -> None)
  }

  def connectionClosed(connection: ConnectionHandle): Unit = {
    connections -= connection
    server.unsubscribeEvents(connection.toString)
    transactions.remove(connection)
  }

  override def afterRequest(connection: ConnectionHandle, response: DataResponse): DataResponse = {
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
              DataResponse(Left(LogicError))
          }
        }

      case Left(_) =>
        transactions.remove(connection)
        response
    }
  }

  def handleRequest(connection: ConnectionHandle, request: Object): DataResponse = {
    val result = connections.get(connection) match {
      case None =>
        logger.error(s"Received message on unbound connection [$connection]")
        Left(InvalidStateError)

      case Some(identifier) =>
        Try { request.asInstanceOf[ServerRequest] } match {
          case Success(request) =>
            identifier match {
              case None => authenticate(connection, request)
              case Some(user) => executeRequest(connection, user, request)
            }
          case Failure(ex) =>
            logger.warn(s"Unable to read message.", ex)
            Left(InvalidMessage)
        }
    }
    DataResponse(result)
  }

  private def authenticate(connection: ConnectionHandle, request: ServerRequest): Either[ResponseException, Any] = {
    request match {
      case CommandRequest(Authenticate(userName)) =>
        db.getUserByName(userName) match {
          case Right(user) =>
            server.subscribeEvents(connection.toString, event => connection.sendEvent(event))
            connections = connections.updated(connection, Some(user))
            hooks.onAuthenticated(startTransaction(connection), user)
            Right(true)

          case Left(NotFound()) =>
            logger.info(s"User [$userName] not found")
            Left(InvalidParameters(s"User [$userName] not found"))

          case Left(ex) =>
            logger.error("Error in server logic", ex)
            Left(LogicError)
        }
      case _ =>
        logger.warn("Received unauthorized request")
        Left(Unauthenticated)
    }
  }

  private def executeRequest(connection: ConnectionHandle, user: User, request: ServerRequest): Either[ResponseException, Any] = {
    try {
      val transaction = startTransaction(connection)
      val response = request match {
        case CommandRequest(command) => server.handleCommand(Request(user, transaction, command))
        case QueryRequest(query) => server.handleQuery(Request(user, transaction, query))
      }
      response match {
        case Failure(ex) =>
          // TODO: is LogicException the correct error here?
          logger.error("Error in server logic", ex)
          Left(LogicError)

        case Success(response) => Right(response)
      }
    } catch {
      case ex: Exception =>
        logger.error("Unexpected server error", ex)
        Left(LogicError)
    }
  }

  private def startTransaction(connection: ConnectionHandle): DbIO with Transaction = {
    val transaction = db.newTransaction
    transactions.put(connection, transaction)
    transaction
  }
}
