package server.actions.record

import javax.inject.Singleton
import nl.roelofruis.artamus.core.model.recording.{RawMidiNote, Recording}
import nl.roelofruis.artamus.core.model.workspace.User.UserId

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

  def getRecording(userId: UserId): Option[Recording] = activeRecordings.get(userId)
  def getAndResetRecording(userId: UserId): Option[Recording] = activeRecordings.remove(userId)

}
