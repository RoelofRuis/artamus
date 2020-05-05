package server.infra

import java.util.concurrent.ConcurrentHashMap

import artamus.core.api.Control.Commit
import artamus.core.api.{Command, Request}
import artamus.core.model.workspace.User
import javax.inject.{Inject, Singleton}

@Singleton
class CommandCollector @Inject() (
  taskScheduler: TaskExecutor
) {

  private val userCommands: ConcurrentHashMap[User, List[Command]] = new ConcurrentHashMap[User, List[Command]]()

  def handle(user: User, request: Request): Unit = {
    request match {
      case Commit() =>
        val commands = Option(userCommands.remove(user)) match {
          case Some(commands) => commands
          case None => List()
        }
        taskScheduler.scheduleCommands(user, commands)

      case c: Command =>
        userCommands.put(user, userCommands.getOrDefault(user, List()) :+ c)
    }
  }
}
