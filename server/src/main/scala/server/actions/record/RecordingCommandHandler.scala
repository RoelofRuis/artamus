package server.actions.record

import domain.interact.Record.{ClearRecording, Quantize, RecordNote}
import domain.math.temporal.{Duration, Position, Window}
import domain.primitives.{Note, NoteGroup}
import domain.record.Quantizer
import domain.write.layers.{NoteLayer, RhythmLayer}
import javax.inject.{Inject, Singleton}
import server.async.{ActionRegistration, ActionRequest}

@Singleton
private[server] class RecordingCommandHandler @Inject() (
  registry: ActionRegistration,
  storage: RecordingStorage,
) {

  registry.register[ClearRecording] { task =>
    storage.startRecording(task.user.id)

    ActionRequest.ok
  }

  registry.register[RecordNote] { task =>
    storage.recordNote(task.user.id, task.attributes.note)

    ActionRequest.ok
  }

  // TODO: extract track writing logic
  import domain.record.Quantization._
  import domain.write.analysis.TwelveToneTuning._
  import server.model.Tracks._
  import server.model.Workspaces._
  registry.register[Quantize] { task =>
    storage.getRecording(task.user.id) match {
      case None => ActionRequest.ok
      case Some(recording) =>
        if (recording.notes.nonEmpty) {
          val quantized = task.attributes.customQuantizer.getOrElse(Quantizer()).quantize(recording.notes.map(_.starts))
          val notePositions: Map[Position, (Seq[Note], Duration)] = recording
            .notes
            .zip(quantized.zip(quantized.drop(1).appended(quantized.last + task.attributes.lastNoteDuration)))
            .foldLeft(Map[Position, (Seq[Note], Duration)]()) { case (acc, (rawMidiNote, (position, nextPosition))) =>
              val duration = nextPosition - position // TODO: determine proper duration
              val note = Note(rawMidiNote.noteNumber.toOct, rawMidiNote.noteNumber.toPc)
              acc.updatedWith(position) {
                case None => Some(Seq(note), Seq(Duration.ZERO, duration).max)
                case Some((seq, prevDur)) => Some(seq :+ note, Seq(duration, prevDur).max)
              }
            }

          val newLayer = if (task.attributes.rhythmOnly) notePositions.foldLeft(RhythmLayer()) {
            case (l, (pos, (notes, duration))) => l.writeNoteGroup(NoteGroup(Window(pos, duration), notes))
          }
          else notePositions.foldLeft(NoteLayer()) {
            case (l, (pos, (notes, duration))) => l.writeNoteGroup(NoteGroup(Window(pos, duration), notes))
          }

          val res = for {
            workspace <- task.db.getWorkspaceByOwner(task.user)
            currentTrack <- task.db.getTrackById(workspace.editingTrack)
            newTrack = currentTrack.appendLayerData(newLayer)
            _ <- task.db.saveTrack(newTrack)
          } yield ()

          ActionRequest.handled(res)
        }
        else ActionRequest.ok
    }
  }

}
