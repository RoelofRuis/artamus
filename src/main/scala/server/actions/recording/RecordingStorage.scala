package server.actions.recording

import javax.inject.Singleton
import music.model.record.{RawMidiNote, Recording}
import music.model.write.user.User.UserId
import music.primitives.TickResolution

import scala.collection.concurrent.TrieMap

@Singleton
class RecordingStorage() {

  private val activeRecordings = new TrieMap[UserId, Recording]

  def startRecording(userId: UserId, resolution: TickResolution): Unit = {
    activeRecordings.put(userId, Recording(resolution))
  }

  def recordNote(userId: UserId, note: RawMidiNote): Unit = {
    activeRecordings.updateWith(userId) {
      case None => None
      case Some(r) => Some(r.recordNote(note))
    }
  }

}
