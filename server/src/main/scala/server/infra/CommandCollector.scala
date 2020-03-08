package server.infra

import java.util.concurrent.ConcurrentHashMap

import domain.interact.Control.Commit
import domain.interact.{Command, Request}
import domain.workspace.User
import javax.inject.Inject

class CommandCollector @Inject() (
  taskScheduler: TaskScheduler
) {

  private val userCommands: ConcurrentHashMap[User, List[Command]] = new ConcurrentHashMap[User, List[Command]]()

  def handle(user: User, request: Request): Unit = {
    request match {
      case Commit(taskId) =>
        val commands = Option(userCommands.remove(user)) match {
          case Some(commands) => commands
          case None => List()
        }
        taskScheduler.scheduleCommands(taskId, user, commands)

      case c: Command =>
        userCommands.put(user, userCommands.getOrDefault(user, List()) :+ c)
    }
  }
}
