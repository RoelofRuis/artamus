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
import pubsub.{Dispatcher, EventBus}
import storage.api.{Database, DbIO, NotFound, Transaction}

import scala.util.{Failure, Success, Try}

@Singleton
final class DispatchingServerAPI @Inject() (
  db: Database,
  hooks: ConnectionLifetimeHooks,
  eventBus: EventBus[Event],
  commandHandler: Dispatcher[Request, Command],
  queryHandler: Dispatcher[Request, Query],
) extends ServerAPI with LazyLogging {

  private var connections: Map[ConnectionHandle, Option[User]] = Map()
  private val transactions: ConcurrentHashMap[ConnectionHandle, Transaction] = new ConcurrentHashMap[ConnectionHandle, Transaction]()

  override def serverStarted(): Unit = logger.info("Server started")

  override def serverShuttingDown(error: Option[Throwable]): Unit = {
    error match {
      case None => logger.info("Server stopped")
      case Some(ex) => logger.error("Server stopped with error", ex)
    }
  }

  override def connectionOpened(connection: ConnectionHandle): Unit = {
    logger.info(s"Connection [$connection] opened")
    connections += (connection -> None)
  }

  override def connectionClosed(connection: ConnectionHandle, cause: Option[Throwable]): Unit = {
    connections -= connection
    eventBus.unsubscribe(connection.toString)
    transactions.remove(connection)
    cause match {
      case None => logger.info(s"Connection [$connection] closed")
      case Some(ex) => logger.warn(s"Connection [$connection] closed with error", ex)
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

  override def afterRequest(connection: ConnectionHandle, response: DataResponse): DataResponse = {
    response.data match {
      case Right(_) =>
        Option(transactions.get(connection)) match {
          case None => response

          case Some(transaction) => transaction.commit() match {
            case Right(numChanges) if numChanges > 0 =>
              logger.debug(s"Committed [$numChanges] changes")
              response

            case Right(numChanges) if numChanges == 0 =>
              logger.debug(s"No changes to commit")
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

  private def authenticate(connection: ConnectionHandle, request: ServerRequest): Either[ResponseException, Any] = {
    request match {
      case CommandRequest(Authenticate(userName)) =>
        db.getUserByName(userName) match {
          case Right(user) =>
            eventBus.subscribe(connection.toString, event => connection.sendEvent(event))
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
        case CommandRequest(command) => commandHandler.handle(Request(user, transaction, command))
        case QueryRequest(query) => queryHandler.handle(Request(user, transaction, query))
      }
      response match {
        case Failure(ex) =>
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
