package music.domain.workspace

import javax.annotation.concurrent.GuardedBy
import javax.inject.Inject
import music.domain.track.Track.TrackId
import music.domain.track.{Track, TrackRepository}
import music.domain.user.User.UserId
import music.domain.workspace.WorkspaceRepository.WorkspaceImpl

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

}

object WorkspaceRepository {

  final case class WorkspaceImpl(
    owner: UserId,
    trackRepository: TrackRepository,
    editedTrackId: TrackId
  ) extends Workspace {
    override def editedTrack: Track = {
      trackRepository.getById(editedTrackId).get // TODO: try to remove 'get'
    }
  }

}