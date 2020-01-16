package server.actions.recording

import javax.inject.Singleton
import music.analysis.Quantization
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
    activeRecordings.get(userId).foreach { r => // TODO: remove printing
      val q = Quantization.millisToPosition(r.notes.map(n => (n.starts.v / 1000).toInt))
      println(q)
    }
  }

}
