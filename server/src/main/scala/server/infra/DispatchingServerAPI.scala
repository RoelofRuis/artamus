package server.infra

import java.util.concurrent.ConcurrentHashMap

import _root_.server.ServerRequest
import com.typesafe.scalalogging.LazyLogging
import domain.interact.Control.Authenticate
import domain.interact.{Event, Request}
import domain.workspace.User
import javax.inject.{Inject, Singleton}
import protocol.Exceptions._
import protocol.server.api.{ConnectionHandle, ServerAPI}
import storage.api.{Database, DbIO, NotFound, Transaction}

import scala.util.{Failure, Success}

@Singleton
final class DispatchingServerAPI @Inject() (
  db: Database,
  hooks: ConnectionLifetimeHooks,
  eventBus: ServerEventBus,
  serverDispatcher: ServerDispatcher
) extends ServerAPI[Request, Event] with LazyLogging {

  import server.model.Users._

  private var connections: Map[ConnectionHandle[Event], Option[User]] = Map()
  private val transactions: ConcurrentHashMap[ConnectionHandle[Event], Transaction] = new ConcurrentHashMap[ConnectionHandle[Event], Transaction]()

  override def serverStarted(): Unit = {
    hooks.onServerStarted()
    logger.info("Server started")
  }

  override def serverShuttingDown(error: Option[Throwable]): Unit = {
    error match {
      case None => logger.info("Server stopped")
      case Some(ex) => logger.error("Server stopped with error", ex)
    }
  }

  override def connectionOpened(connection: ConnectionHandle[Event]): Unit = {
    logger.info(s"Connection [$connection] opened")
    connections += (connection -> None)
  }

  override def connectionClosed(connection: ConnectionHandle[Event], cause: Option[Throwable]): Unit = {
    connections -= connection
    eventBus.unsubscribe(connection.toString)
    transactions.remove(connection)
    cause match {
      case None => logger.info(s"Connection [$connection] closed")
      case Some(ex) => logger.warn(s"Connection [$connection] closed with error", ex)
    }
  }

  def handleReceiveFailure(connection: ConnectionHandle[Event], cause: Throwable): ResponseException = {
    logger.warn(s"Received invalid message on connection [$connection]", cause)
    InvalidMessage
  }

  def handleRequest(connection: ConnectionHandle[Event], request: Request): Either[ResponseException, Any] = {
    connections.get(connection) match {
      case None =>
        logger.error(s"Received message on unbound connection [$connection]")
        Left(InvalidStateError)

      case Some(identifier) =>
          identifier match {
            case None => authenticate(connection, request)
            case Some(user) => executeRequest(connection, user, request)
          }
        }
    }

  override def afterRequest(connection: ConnectionHandle[Event], response: Either[ResponseException, Any]): Either[ResponseException, Any] = {
    response match {
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
              Left(LogicError)
          }
        }

      case Left(_) =>
        transactions.remove(connection)
        response
    }
  }

  private def authenticate(connection: ConnectionHandle[Event], request: Request): Either[ResponseException, Any] = {
    request match {
      case Authenticate(userName) =>
        db.getUserByName(userName) match {
          case Right(user) =>
            eventBus.subscribe(connection.toString, event => connection.sendEvent(event))
            connections = connections.updated(connection, Some(user))
            hooks.onAuthenticated(user)
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

  private def executeRequest(connection: ConnectionHandle[Event], user: User, request: Request): Either[ResponseException, Any] = {
    try {
      val transaction = startTransaction(connection)
      serverDispatcher.handle(ServerRequest(user, transaction, request)) match {
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

  private def startTransaction(connection: ConnectionHandle[Event]): DbIO with Transaction = {
    val transaction = db.newTransaction
    transactions.put(connection, transaction)
    transaction
  }
}
