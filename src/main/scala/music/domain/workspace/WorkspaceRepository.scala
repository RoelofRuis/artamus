package music.domain.workspace

import javax.annotation.concurrent.GuardedBy
import javax.inject.{Inject, Singleton}
import music.domain.track.Track.TrackId
import music.domain.track.{Track, TrackRepository}
import music.domain.user.User.UserId
import music.domain.workspace.WorkspaceRepository.WorkspaceImpl

@Singleton
class WorkspaceRepository @Inject() (trackRepository: TrackRepository) {

  private val workspaceLock = new Object()
  @GuardedBy("workspaceLock") private var workspaces: Map[UserId, Workspace] = Map()

  def getByOwner(userId: UserId): Workspace = workspaceLock.synchronized {
    if (workspaces.isDefinedAt(userId)) workspaces(userId)
    else {
      val trackId = trackRepository.write(Track())
      val newWorkspace = WorkspaceImpl(
        userId,
        trackRepository,
        trackId
      )
      workspaces += (userId -> newWorkspace)
      newWorkspace
    }
  }

  def write(workspace: Workspace): Unit = workspaceLock.synchronized {
    workspaces = workspaces.updated(workspace.owner, workspace)
  }

}

object WorkspaceRepository {

  final case class WorkspaceImpl(
    owner: UserId,
    trackRepository: TrackRepository,
    editedTrackId: TrackId
  ) extends Workspace {
    override def getEditedTrack: Track = {
      trackRepository.getById(editedTrackId).get // TODO: try to remove 'get'
    }
    override def startNewEdit: Workspace = {
      val newTrackId = trackRepository.write(Track())
      WorkspaceImpl(owner, trackRepository, newTrackId)
    }
  }

}