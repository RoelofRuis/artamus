package music.domain.workspace

import music.domain.track.Track
import music.domain.user.User
import music.domain.user.User.UserId

trait WorkspaceRepository {

  def getByOwner(user: User): Workspace

  def put(workspace: Workspace): Unit

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