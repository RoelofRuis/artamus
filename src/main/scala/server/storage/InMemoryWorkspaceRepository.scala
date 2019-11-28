package server.storage

import javax.annotation.concurrent.GuardedBy
import javax.inject.{Inject, Singleton}
import music.domain.user.User
import music.domain.user.User.UserId
import music.domain.workspace.{Workspace, WorkspaceRepository}

import scala.util.{Failure, Success, Try}

@Singleton
class InMemoryWorkspaceRepository @Inject() extends WorkspaceRepository {

  private val workspaceLock = new Object()
  @GuardedBy("workspaceLock") private var workspaces: Map[UserId, Workspace] = Map()

  def getByOwner(user: User): Try[Workspace] = workspaceLock.synchronized {
    workspaces.get(user.id) match {
      case Some(workspace) => Success(workspace)
      case None => Failure(EntityNotFoundException("Workspace"))
    }
  }

  def put(workspace: Workspace): Try[Unit] = workspaceLock.synchronized {
    workspaces = workspaces.updated(workspace.owner, workspace)
    Success(())
  }

}

