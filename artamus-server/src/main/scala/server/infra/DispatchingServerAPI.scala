package server.infra

import com.typesafe.scalalogging.LazyLogging
import artamus.core.api.Control.Authenticate
import artamus.core.api.{Command, Event, Query, Request}
import artamus.core.model.workspace.User
import javax.inject.{Inject, Singleton}
import network.Exceptions._
import network.server.api.{ConnectionHandle, ServerAPI}
import server.api.{QueryDispatcher, QueryRequest, ServerEventBus}
import storage.api.{Database, NotFound}

import scala.util.{Failure, Success}

@Singleton
final class DispatchingServerAPI @Inject() (
  db: Database,
  hooks: ConnectionLifetimeHooks,
  eventBus: ServerEventBus,
  queryDispatcher: QueryDispatcher,
  commandCollector: CommandCollector
) extends ServerAPI[Request, Event] with LazyLogging {

  import server.model.Users._

  private var connections: Map[ConnectionHandle[Event], Option[User]] = Map()

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
    cause match {
      case None => logger.info(s"Connection [$connection] closed")
      case Some(ex) => logger.warn(s"Connection [$connection] closed with error", ex)
    }
  }

  override def receiveFailed(connection: ConnectionHandle[Event], cause: Throwable): Unit = {
    logger.warn(s"Received invalid message on connection [$connection]", cause)
  }

  def handleRequest(connection: ConnectionHandle[Event], request: Request): Either[ResponseException, Any] = {
    connections.get(connection) match {
      case None =>
        logger.error(s"Received message on unbound connection [$connection]")
        Left(InvalidStateError)

      case Some(identifier) =>
          identifier match {
            case None => authenticate(connection, request)
            case Some(user) => executeRequest(user, request)
          }
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

  private def executeRequest(user: User, request: Request): Either[ResponseException, Any] = {
    try {
      request match {
        case q: Query => queryDispatcher.handle(QueryRequest(user, db, q)) match {
          case Failure(ex) =>
            logger.error("Error in server logic", ex)
            Left(LogicError)

          case Success(response) => Right(response)
        }
        case _: Command =>
          commandCollector.handle(user, request)
          Right(())
      }
    } catch {
      case ex: Exception =>
        logger.error("Unexpected server error", ex)
        Left(LogicError)
    }
  }
}
