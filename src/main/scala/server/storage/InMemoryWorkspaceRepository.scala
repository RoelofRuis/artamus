package server.storage

import javax.annotation.concurrent.GuardedBy
import javax.inject.{Inject, Singleton}
import music.domain.user.User
import music.domain.user.User.UserId
import music.domain.workspace.{Workspace, WorkspaceRepository}

import scala.util.{Success, Try}

@Singleton
class InMemoryWorkspaceRepository @Inject() extends WorkspaceRepository {

  private val workspaceLock = new Object()
  @GuardedBy("workspaceLock") private var workspaces: Map[UserId, Workspace] = Map()

  def getByOwner(user: User): Try[Workspace] = workspaceLock.synchronized {
    if (workspaces.isDefinedAt(user.id)) Success(workspaces(user.id))
    else {
      val newWorkspace = Workspace(user.id, None)
      workspaces += (user.id -> newWorkspace)
      Success(newWorkspace)
    }
  }

  def put(workspace: Workspace): Try[Unit] = workspaceLock.synchronized {
    workspaces = workspaces.updated(workspace.owner, workspace)
    Success(())
  }

}

