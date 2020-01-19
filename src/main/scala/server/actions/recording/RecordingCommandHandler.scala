package server.actions.recording

import javax.inject.{Inject, Singleton}
import music.analysis.Quantization
import music.math.Rational
import music.math.temporal.{Duration, Position, Window}
import music.model.write.track.Track
import music.primitives.{Note, NoteGroup}
import protocol.Command
import pubsub.Dispatcher
import server.Request
import server.actions.Responses

@Singleton
private[server] class RecordingCommandHandler @Inject() (
  dispatcher: Dispatcher[Request, Command],
  storage: RecordingStorage,
) {

  dispatcher.subscribe[StartRecording] { req =>
    storage.startRecording(req.user.id)

    Responses.ok
  }

  dispatcher.subscribe[RecordNote] { req =>
    storage.recordNote(req.user.id, req.attributes.note)

    Responses.ok
  }

  // TODO: extract track writing logic
  import music.analysis.TwelveToneTuning._
  import server.model.Tracks._
  import server.model.Workspaces._
  dispatcher.subscribe[StopRecording] { req =>
    storage.getAndResetRecording(req.user.id) match {
      case None => Responses.ok
      case Some(recording) =>
        val quantized = Quantization.millisToPosition(recording.notes.map(n => (n.starts.v / 1000).toInt))
        val recordedTrack = recording
          .notes
          .zip(quantized.zip(quantized.drop(1).appended(quantized.last + Duration(Rational(1, 4)))))
          .foldLeft(Map[Position, (Seq[Note], Duration)]()) { case (acc, (rawMidiNote, (position, nextPosition))) =>
            val duration = nextPosition - position // TODO: determine proper duration
            val note = Note(rawMidiNote.noteNumber.toOct, rawMidiNote.noteNumber.toPc)
            acc.updatedWith(position) {
              case None => Some(Seq(note), Seq(Duration.ZERO, duration).max)
              case Some((seq, prevDur)) => Some(seq :+ note, Seq(duration, prevDur).max)
            }
          }
          .foldLeft(Track()) { case (track, (position, (notes, duration))) =>
            track.writeNoteGroup(NoteGroup(Window(position, duration), notes))
          }

        val res = for {
          workspace <- req.db.getWorkspaceByOwner(req.user)
          newWorkspace = workspace.selectTrack(recordedTrack) // TODO: remove old track!
          _ <- req.db.saveTrack(recordedTrack)
          _ <- req.db.saveWorkspace(newWorkspace)
        } yield ()

        Responses.executed(res)
    }
  }

}
