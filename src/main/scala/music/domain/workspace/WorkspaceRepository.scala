package music.domain.workspace

import javax.annotation.concurrent.GuardedBy
import javax.inject.{Inject, Singleton}
import music.domain.track.{Track, TrackRepository}
import music.domain.user.User
import music.domain.user.User.UserId
import music.domain.workspace.WorkspaceRepository.WorkspaceImpl

@Singleton
class WorkspaceRepository @Inject() (trackRepository: TrackRepository) {

  private val workspaceLock = new Object()
  @GuardedBy("workspaceLock") private var workspaces: Map[UserId, Workspace] = Map()

  def getByOwner(user: User): Workspace = workspaceLock.synchronized {
    if (workspaces.isDefinedAt(user.id)) workspaces(user.id)
    else {
      val newWorkspace = WorkspaceImpl(
        user.id,
        Track()
      )
      workspaces += (user.id -> newWorkspace)
      newWorkspace
    }
  }

  def write(workspace: Workspace): Unit = workspaceLock.synchronized {
    workspaces = workspaces.updated(workspace.owner, workspace)
    trackRepository.write(workspace.editedTrack)
  }

}

object WorkspaceRepository {

  final case class WorkspaceImpl(
    owner: UserId,
    editedTrack: Track,
    annotatedTrack: Option[Track] = None
  ) extends Workspace {
    override def startNewEdit: Workspace = copy(editedTrack = Track())
    override def makeEdit(track: Track): Workspace = copy(editedTrack = track)
    override def makeAnnotations(track: Track): Workspace = copy(annotatedTrack = Some(track))
    override def useAnnotations: Workspace = {
      annotatedTrack match {
        case None => this
        case Some(annotated) => copy(editedTrack = annotated)
      }
    }
  }

}