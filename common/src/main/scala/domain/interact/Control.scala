package domain.interact

import java.util.UUID

object Control {

  final case class Disconnect() extends Command
  final case class Authenticate(userName: String) extends Command

  // Tasks
  final case class TaskId private (id: UUID)
  object TaskId { def apply(): TaskId = TaskId(UUID.randomUUID()) }

  final case class Commit(taskId: TaskId = TaskId()) extends Command // TODO: refactor so starting task is also sent to client
  final case class TaskSuccessful(taskId: TaskId) extends Event
  final case class TaskFailed(taskId: TaskId, error: Throwable) extends Event

}
