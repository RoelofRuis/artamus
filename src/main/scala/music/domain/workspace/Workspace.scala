package music.domain.workspace

import music.domain.track.Track
import music.domain.user.User.UserId

trait Workspace {
  val owner: UserId
  def editedTrack: Option[Track]
}

object Workspace {

  final case class WorkspaceId(id: Long) extends AnyVal

}