package artamus.core.api

import java.util.UUID

object Control {

  final case class Disconnect() extends Command
  final case class Authenticate(userName: String) extends Command

  // Tasks
  final case class TaskId(id: UUID = UUID.randomUUID())

  final case class Commit() extends Command
  final case class TaskStarted(taskId: TaskId) extends Event
  final case class TaskSuccessful(taskId: TaskId) extends Event
  final case class TaskFailed(taskId: TaskId, error: Throwable) extends Event

}
