package server.infra

import java.util.concurrent.{ExecutorService, Executors}

import com.typesafe.scalalogging.LazyLogging
import nl.roelofruis.artamus.core.api.Control.{TaskFailed, TaskId, TaskStarted, TaskSuccessful}
import nl.roelofruis.artamus.core.api.{Command, Event}
import nl.roelofruis.artamus.core.model.workspace.User
import javax.inject.{Inject, Singleton}
import server.api.{CommandRequest, ServerEventBus}
import server.infra.TaskExecutor.TaskResult
import storage.api.Database

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class TaskExecutor @Inject() (
  db: Database,
  registry: CommandHandlerRegistry,
  eventBus: ServerEventBus,
) extends LazyLogging {

  private val executor: ExecutorService = Executors.newFixedThreadPool(1, (r: Runnable) => {
    val t: Thread = Executors.defaultThreadFactory().newThread(r);
    t.setDaemon(true);
    t
  })

  private implicit val executionContext: ExecutionContext = ExecutionContext.fromExecutor(executor)

  def scheduleCommands(user: User, commands: List[Command]): Unit = {
    val taskId = TaskId()
    eventBus.publish(TaskStarted(taskId))

    logger.debug(s"Scheduling commands for task [$taskId]")

    Future {
      val transaction = db.newTransaction
      val result = commands.foldLeft(TaskResult()) { case (result, command) =>
        if (result.error.isDefined) result.copy(skipped = result.skipped :+ command)
        else {
          registry.lookupHandler(command) match {
            case None => result.copy(error = Some((command, new Throwable("Missing Handler"))))
            case Some(handler) =>
              logger.debug(s"Handling command [$command]")
              handler(CommandRequest(user, transaction, command)) match {
                case Failure(exception) =>
                  logger.debug(s"Handling failed")
                  result.copy(error = Some((command, exception)))
                case Success(taskEvents) =>
                  logger.debug(s"Handling success")
                  result.copy(done = result.done :+ command, events = result.events ++ taskEvents)
              }
          }
        }
      }

      result.error match {
        case None => transaction.commit() match {
          case Right(_) => result.copy(events = result.events :+ TaskSuccessful(taskId))
          case Left(ex) => result.copy(events = List(TaskFailed(taskId, ex)))
        }
        case Some(ex) => result.copy(events = List(TaskFailed(taskId, ex._2)))
      }
    }.onComplete {
      case Success(taskResult) =>
        logDebugReport(taskId, taskResult)
        taskResult.events.foreach(eventBus.publish)

      case Failure(ex) =>
        logger.warn(s"Task Thread failed for task [$taskId]", ex)
        eventBus.publish(TaskFailed(taskId, ex))
    }
  }

  private def logDebugReport(taskId: TaskId, result: TaskResult): Unit = {
    if (result.error.isEmpty) {
      logger.debug(
        s"""Report for task [$taskId]
           |# Done    [${result.done.length}]
           |# Events  [${result.events.length}]
           |""".stripMargin
      )
    } else {
      logger.warn(
        s"""Report for <FAILED> task [$taskId]
           |# Done    [${result.done.length}]
           |# Skipped [${result.skipped.length}]
           |
           |Error:
           |Command   [${result.error.get._1}]
           |Cause     [${result.error.get._2}]
           |""".stripMargin, result.error.get._2
      )
    }
  }

}

object TaskExecutor {

  final case class TaskResult(
    done: List[Command] = List(),
    skipped: List[Command] = List(),
    error: Option[(Command, Throwable)] = None, // TODO: determine error types (what are the places that error is set?)
    events: List[Event] = List()
  )

}
