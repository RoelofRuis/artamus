package server.async

import java.util.concurrent.{ExecutorService, Executors}

import domain.interact.Control.{TaskFailed, TaskId, TaskSuccessful}
import domain.interact.{Command, Event}
import domain.workspace.User
import javax.inject.Inject
import server.async.TaskScheduler.TaskResult
import server.infra.ServerEventBus
import storage.api.Database

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class TaskScheduler @Inject() (
  db: Database,
  registry: CommandHandlerRegistry,
  eventBus: ServerEventBus,
) {

  private val executor: ExecutorService = Executors.newFixedThreadPool(1, (r: Runnable) => {
    val t: Thread = Executors.defaultThreadFactory().newThread(r);
    t.setDaemon(true);
    t
  })

  private implicit val executionContext: ExecutionContext = ExecutionContext.fromExecutor(executor)

  def scheduleCommands(taskId: TaskId, user: User, commands: List[Command]): Unit = {
    Future {
      val transaction = db.newTransaction
      val result = commands.foldRight(TaskResult()) { case (command, result) =>
        if (result.error.isDefined) result.copy(skipped = result.skipped :+ command)
        else {
          registry.lookupHandler(command) match {
            case None => result.copy(error = Some((command, new Throwable("Missing Handler"))))
            case Some(handler) =>
              handler(CommandRequest(user, transaction, command)) match {
                case Failure(exception) =>
                  result.copy(error = Some((command, exception)))
                case Success(taskEvents) =>
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
      // TODO: print debug report
      case Success(taskResult) => taskResult.events.foreach(eventBus.publish)
      case Failure(ex) => eventBus.publish(TaskFailed(taskId, ex))
    }

  }

}

object TaskScheduler {

  final case class TaskResult(
    done: List[Command] = List(),
    skipped: List[Command] = List(),
    error: Option[(Command, Throwable)] = None, // TODO: determine error types (what are the places that error is set?)
    events: List[Event] = List()
  )

}
