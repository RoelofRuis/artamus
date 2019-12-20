package music.model.write.workspace

import music.model.record.Recording
import music.model.record.Recording.RecordingId
import music.model.write.track.Track
import music.model.write.track.Track.TrackId
import music.model.write.user.User.UserId

final case class Workspace(
  owner: UserId,
  selectedTrack: TrackId,
  activeRecording: Option[RecordingId] = None
) {
  def selectTrack(track: Track): Workspace = copy(selectedTrack = track.id)
  def startRecording(recording: Recording): Workspace = copy(activeRecording = Some(recording.id))
}

object Workspace {

  final case class WorkspaceId(id: Long) extends AnyVal

}