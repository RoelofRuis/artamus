package server.actions.recording

import javax.inject.Singleton
import music.model.record.{RawMidiNote, Recording}
import music.model.write.user.User.UserId

import scala.collection.concurrent.TrieMap

@Singleton
class RecordingStorage() {

  // TODO: add map that stores starts of notes

  private val activeRecordings = new TrieMap[UserId, Recording]

  def startRecording(userId: UserId): Unit = {
    activeRecordings.put(userId, Recording())
  }

  def recordNote(userId: UserId, note: RawMidiNote): Unit = {
    activeRecordings.updateWith(userId) {
      case None => None
      case Some(r) => Some(r.recordNote(note))
    }
  }

  def getAndResetRecording(userId: UserId): Option[Recording] = {
    activeRecordings.remove(userId)
  }

}
