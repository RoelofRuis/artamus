package server.storage

import javax.annotation.concurrent.GuardedBy
import javax.inject.{Inject, Singleton}
import music.domain.track.{Track, TrackRepository}
import music.domain.user.User
import music.domain.user.User.UserId
import music.domain.workspace.{Workspace, WorkspaceRepository}

@Singleton
class InMemoryWorkspaceRepository @Inject() (trackRepository: TrackRepository) extends WorkspaceRepository {

  private val workspaceLock = new Object()
  @GuardedBy("workspaceLock") private var workspaces: Map[UserId, Workspace] = Map()

  def getByOwner(user: User): Workspace = workspaceLock.synchronized {
    if (workspaces.isDefinedAt(user.id)) workspaces(user.id)
    else {
      val newWorkspace = Workspace(
        user.id,
        Track()
      )
      workspaces += (user.id -> newWorkspace)
      newWorkspace
    }
  }

  def put(workspace: Workspace): Unit = workspaceLock.synchronized {
    workspaces = workspaces.updated(workspace.owner, workspace)
    trackRepository.write(workspace.editedTrack)
  }

}

